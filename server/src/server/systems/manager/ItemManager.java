package server.systems.manager;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.annotations.Wire;
import entity.character.attributes.Agility;
import entity.character.attributes.Attribute;
import entity.character.attributes.Strength;
import entity.character.info.Inventory;
import entity.character.states.Buff;
import entity.character.status.Health;
import entity.character.status.Mana;
import server.core.Server;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.objects.types.Obj;
import shared.objects.types.ObjWithClasses;
import shared.objects.types.PotionObj;
import shared.objects.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.artemis.E.E;

/**
 * It keeps logic regarding items, how to use, to know if they are 'usable' or 'equipable'
 */
@Wire
public class ItemManager extends DefaultManager {

    private ItemConsumers itemConsumers;
    private ObjectManager objectManager;
    private WorldManager worldManager;

    public ItemManager() {
    }

    @Override
    public void initialize() {
    }

    public ItemConsumers getItemConsumers() {
        return itemConsumers;
    }

    public boolean isEquippable(Inventory.Item item) {
        Optional<Obj> object = objectManager.getObject(item.objId);
        if (object.isPresent()) {
            Obj obj = object.get();
            return obj instanceof ObjWithClasses;
        }
        return false;
    }

    public boolean isUsable(Inventory.Item item) {
        Optional<Obj> object = objectManager.getObject(item.objId);
        return object.map(obj -> obj.getType().equals(Type.POTION)).orElse(false);
    }

    public void use(int player, Inventory.Item item) {
        Optional<Obj> object = objectManager.getObject(item.objId);
        object.ifPresent(obj -> {
            if (obj.getType().equals(Type.POTION)) {
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
                        SendAttributeUpdate(player, agility, E(player).getBuff());
                        break;
                    case POISON:
                    case STRENGTH:
                        Strength strength = E(player).getStrength();
                        strength.setCurrentValue(strength.getBaseValue() + random);
                        E(player).buff().getBuff().addAttribute(strength, potion.getEffecTime());
                        SendAttributeUpdate(player, strength, E(player).getBuff());
                        break;
                }
                // Notify update to user
                EntityUpdate update = EntityUpdateBuilder.of(player).withComponents(components.toArray(new Component[0])).build();
                worldManager.sendEntityUpdate(player, update);
                // TODO remove from inventory
            }
        });
    }

    protected void SendAttributeUpdate(int player, Attribute attribute, Buff buff) {
        EntityUpdate updateAGI = EntityUpdateBuilder.of(E(player).id()).withComponents(attribute, buff).build();
        worldManager.sendEntityUpdate(player, updateAGI);
    }

    public void equip(int player, int index, Inventory.Item item) {
        InventoryUpdate update = new InventoryUpdate();
        modifyUserEquip(player, item, index, update);
        worldManager.sendEntityUpdate(player, update);
    }

    private void modifyUserEquip(int player, Inventory.Item item, int index, InventoryUpdate update) {
        Optional<Obj> object = objectManager.getObject(item.objId);
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
        (equipped ? itemConsumers.WEAR : itemConsumers.TAKE_OFF).accept(player, item);
    }

    private void discardItems(E entity, int index, Type type, InventoryUpdate update) {
        Inventory.Item[] items = entity.getInventory().items;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && index != i) {
                int inventoryIndex = i;
                objectManager.getObject(items[i].objId).ifPresent(obj -> {
                    if (items[inventoryIndex].equipped && obj.getType().equals(type)) {
                        items[inventoryIndex].equipped = false;
                        update.add(inventoryIndex, items[inventoryIndex]);
                    }
                });
            }
        }
    }
}
