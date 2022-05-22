package server.systems.world.entity.item;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.esotericsoftware.minlog.Log;
import component.entity.character.attributes.Agility;
import component.entity.character.attributes.Attribute;
import component.entity.character.attributes.Strength;
import component.entity.character.info.*;
import component.entity.character.states.Buff;
import component.entity.character.status.Health;
import component.entity.character.status.Level;
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

/**
 * It keeps logic regarding items, how to use, to know if they are 'usable' or 'equipable'
 */
public class ItemSystem extends PassiveSystem {

    private ItemUsageSystem itemUsageSystem;
    private ObjectSystem objectSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private SoundEntitySystem soundEntitySystem;

    ComponentMapper<Name> mName;
    ComponentMapper<CharHero> mCharHero;
    ComponentMapper<Health> mHealth;
    ComponentMapper<Mana> mMana;
    ComponentMapper<Level> mLevel;
    ComponentMapper<Gold> mGold;
    ComponentMapper<Buff> mBuff;
    ComponentMapper<Agility> mAgility;
    ComponentMapper<Strength> mStrength;
    ComponentMapper<Bag> mBag;
    ComponentMapper<SpellBook> mSpellBook;

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

    public void use(int playerId, Bag.Item item) {
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
                            Health health = mHealth.get(playerId);
                            health.min = Math.min(health.min + random, health.max);
                            components.add(health);
                            break;
                        case MANA:
                            Mana mana = mMana.get(playerId);
                            final int level = mLevel.get(playerId).getLevel();
                            mana.min += mana.max * 0.04f + (level >> 1) + 40 / level;
                            mana.min = Math.min(mana.min, mana.max);
                            components.add(mana);
                            break;
                        case AGILITY:
                            Agility agility = mAgility.get(playerId);
                            agility.setCurrentValue(agility.getBaseValue() + random);
                            mBuff.create(playerId).addAttribute(agility, potion.getEffecTime());
                            sendAttributeUpdate(playerId, agility, mBuff.get(playerId));
                            break;
                        case POISON:
                        case STRENGTH:
                            Strength strength = mStrength.get(playerId);
                            strength.setCurrentValue(strength.getBaseValue() + random);
                            mBuff.create(playerId).addAttribute(strength, potion.getEffecTime());
                            sendAttributeUpdate(playerId, strength, mBuff.get(playerId));
                            break;
                    }
                    // Notify update to user
                    EntityUpdate update = EntityUpdateBuilder.of(playerId).withComponents(components.toArray(new Component[0])).build();
                    entityUpdateSystem.add(update, UpdateTo.ENTITY);
                    soundEntitySystem.add(playerId, 46);
                    // TODO remove from inventory
                    break;
                case SPELL:
                    SpellObj spellObj = (SpellObj) obj;
                    if (mCharHero.get(playerId).getHeroId() != 0) {
                        mSpellBook.get(playerId).addSpell(spellObj.getSpellIndex());
                    }
                    if (mSpellBook.get(playerId).getMsj().equals("hechiso agregado")) {
                        soundEntitySystem.add(playerId, 109);
                    }
                    Log.info(mName.get(playerId).text + " " + mSpellBook.get(playerId).getMsj());
                    break;
                case GOLD:
                    int goldCount = item.count + mGold.get(playerId).getCount();
                    mGold.get(playerId).setCount(goldCount);
                    removeGold(playerId);
                    break;
            }
        });
    }

    protected void sendAttributeUpdate(int playerId, Attribute attribute, Buff buff) {
        EntityUpdate updateAGI = EntityUpdateBuilder.of(playerId).withComponents(attribute, buff).build();
        entityUpdateSystem.add(updateAGI, UpdateTo.ENTITY);
    }

    private void removeGold(int playerId) {
        Bag.Item[] items = mBag.get(playerId).items;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (objectSystem.getObject(items[i].objId).get().getType().equals(Type.GOLD)) {
                    items[i].count = 0;
                    //notifica el incremento de oro en el jugador
                    EntityUpdateBuilder goldUpdate = EntityUpdateBuilder.of(playerId);
                    goldUpdate.withComponents(mGold.get(playerId));
                    worldEntitiesSystem.sendEntityUpdate(playerId, goldUpdate.build());
                    //notifica la remocion del item y se actualiza el inventario
                    InventoryUpdate update = new InventoryUpdate();
                    update.remove(i);
                    worldEntitiesSystem.sendEntityUpdate(playerId, update);
                    worldEntitiesSystem.sendEntityUpdate(playerId, mGold.get(playerId));
                }
            }
        }
    }

    public void equip(int playerId, int index, Bag.Item item) {
        InventoryUpdate update = new InventoryUpdate();
        // TODO convert InventoryUpdate into EntityUpdate
        modifyUserEquip(playerId, item, index, update);
        worldEntitiesSystem.sendEntityUpdate(playerId, update);
    }

    private void modifyUserEquip(int playerId, Bag.Item item, int index, InventoryUpdate update) {
        Optional<Obj> object = objectSystem.getObject(item.objId);
        object.ifPresent(obj -> {
            item.equipped = !item.equipped;
            update.add(index, item);
            if (item.equipped) {
                discardItems(playerId, index, obj.getType(), update);
            }
            equipItem(playerId, obj, item.equipped);
        });
    }

    private void equipItem(int playerId, Obj item, boolean equipped) {
        (equipped ? itemUsageSystem.WEAR : itemUsageSystem.TAKE_OFF).accept(playerId, item);
    }

    private void discardItems(int playerId, int index, Type type, InventoryUpdate update) {
        Bag.Item[] items = mBag.get(playerId).items;
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
