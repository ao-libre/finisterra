package server.systems.world.entity.item;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.entity.character.attributes.Agility;
import component.entity.character.attributes.Attribute;
import component.entity.character.attributes.Strength;
import component.entity.character.info.Bag;
import component.entity.character.states.Buff;
import component.entity.character.status.Health;
import component.entity.character.status.Mana;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.config.ObjectSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.factory.SoundEntitySystem;
import server.utils.UpdateTo;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;
import shared.objects.types.Obj;
import shared.objects.types.PotionObj;
import shared.objects.types.SpellObj;
import shared.objects.types.Type;
import shared.util.EntityUpdateBuilder;
import shared.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.artemis.E.E;

/**
 * It keeps logic regarding items, how to use, to know if they are 'usable' or 'equipable'
 */
@Wire
public class ItemSystem extends PassiveSystem {

    private ItemUsageSystem itemUsageSystem;
    private ObjectSystem objectSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private SoundEntitySystem soundEntitySystem;

    public ItemSystem() {
    }

    public boolean isEquippable(Bag.Item item) {
        Optional<Obj> object = objectSystem.getObject(item.objId);
        return object.map(ItemUtils::canEquip).orElse(false);
    }

    public boolean isUsable(Bag.Item item) {
        /* TODO evitar que se use el item si esta lleno
            comprobar canUse y comprobar que sean distintos minStatus maxStatus
        */
        Optional<Obj> object = objectSystem.getObject(item.objId);
        return object.map(ItemUtils::canUse).orElse(false);
    }

    public void use(int player, Bag.Item item) {
        Optional<Obj> object = objectSystem.getObject(item.objId);
        object.ifPresent(obj -> {
            Type objType = obj.getType();

            switch (objType) {
                case POTION:
                    PotionObj potion = (PotionObj) obj;
                    int max = potion.getMax();
                    int min = potion.getMin();
                    int random = new Random().nextInt(max - min + 1) + min;
                    List<Component> components = new ArrayList<>();

                    switch (potion.getKind()) {
                        case HP:
                            Health health = E(player).getHealth();
                            health.min = Math.min(health.min + random, health.max);
                            components.add(health);
                            break;
                        case MANA:
                            Mana mana = E(player).getMana();
                            final int level = E(player).levelLevel();
                            mana.min += mana.max * 0.04f + (level >> 1) + 40 / level;
                            mana.min = Math.min(mana.min, mana.max);
                            components.add(mana);
                            break;
                        case AGILITY:
                            Agility agility = E(player).getAgility();
                            agility.setCurrentValue(agility.getBaseValue() + random);
                            E(player).buff().getBuff().addAttribute(agility, potion.getEffecTime());
                            sendAttributeUpdate(player, agility, E(player).getBuff());
                            break;
                        case POISON:
                        case STRENGTH:
                            Strength strength = E(player).getStrength();
                            strength.setCurrentValue(strength.getBaseValue() + random);
                            E(player).buff().getBuff().addAttribute(strength, potion.getEffecTime());
                            sendAttributeUpdate(player, strength, E(player).getBuff());
                            break;
                    }
                    // Notify update to user
                    EntityUpdate update = EntityUpdateBuilder.of(player).withComponents(components.toArray(new Component[0])).build();
                    entityUpdateSystem.add(update, UpdateTo.ENTITY);
                    soundEntitySystem.add(player, 46);
                    // TODO remove from inventory
                    break;
                case SPELL:
                    SpellObj spellObj = (SpellObj) obj;
                    if (E(player).charHeroHeroId() != 0) {
                        E(player).spellBookAddSpell(spellObj.getSpellIndex());
                    }
                    if (E(player).getSpellBook().getMsj().equals("hechiso agregado")) {
                        soundEntitySystem.add(player, 109);

                    }
                    Log.info(E(player).nameText() + " " + E(player).getSpellBook().getMsj());
                    break;
                case GOLD:
                    E playerUser = E(player);
                    int gold = item.count + playerUser.getGold().getCount();
                    playerUser.goldCount(gold);
                    removeGold(player);
                    break;
            }
        });
    }

    protected void sendAttributeUpdate(int player, Attribute attribute, Buff buff) {
        EntityUpdate updateAGI = EntityUpdateBuilder.of(E(player).id()).withComponents(attribute, buff).build();
        entityUpdateSystem.add(updateAGI, UpdateTo.ENTITY);
    }

    private void removeGold(int player) {
        E entity = E(player);
        Bag.Item[] items = entity.getBag().items;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (objectSystem.getObject(items[i].objId).get().getType().equals(Type.GOLD)) {
                    items[i].count = 0;
                    //notifica el incremento de oro en el jugador
                    EntityUpdateBuilder goldUpdate = EntityUpdateBuilder.of(player);
                    goldUpdate.withComponents(E(player).getGold());
                    worldEntitiesSystem.sendEntityUpdate(player, goldUpdate.build());
                    //notifica la remocion del item y se actualiza el inventario
                    InventoryUpdate update = new InventoryUpdate();
                    update.remove(i);
                    worldEntitiesSystem.sendEntityUpdate(player, update);
                    worldEntitiesSystem.sendEntityUpdate(player, E(player).goldCount());
                }
            }
        }
    }

    public void equip(int player, int index, Bag.Item item) {
        InventoryUpdate update = new InventoryUpdate();
        // TODO convert InventoryUpdate into EntityUpdate
        modifyUserEquip(player, item, index, update);
        worldEntitiesSystem.sendEntityUpdate(player, update);
    }

    private void modifyUserEquip(int player, Bag.Item item, int index, InventoryUpdate update) {
        Optional<Obj> object = objectSystem.getObject(item.objId);
        object.ifPresent(obj -> {
            item.equipped = !item.equipped;
            update.add(index, item);
            if (item.equipped) {
                discardItems(E(player), index, obj.getType(), update);
            }
            equipItem(player, obj, item.equipped);
        });
    }

    private void equipItem(int player, Obj item, boolean equipped) {
        (equipped ? itemUsageSystem.WEAR : itemUsageSystem.TAKE_OFF).accept(player, item);
    }

    private void discardItems(E entity, int index, Type type, InventoryUpdate update) {
        Bag.Item[] items = entity.getBag().items;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && index != i) {
                int inventoryIndex = i;
                objectSystem.getObject(items[i].objId).ifPresent(obj -> {
                    if (items[inventoryIndex].equipped && obj.getType().equals(type)) {
                        items[inventoryIndex].equipped = false;
                        update.add(inventoryIndex, items[inventoryIndex]);
                    }
                });
            }
        }
    }
}
