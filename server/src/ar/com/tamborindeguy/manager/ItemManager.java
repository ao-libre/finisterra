package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.objects.types.*;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import entity.Object;
import entity.character.info.Inventory;
import entity.character.status.Health;
import entity.character.status.Mana;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.artemis.E.E;

public class ItemManager {

    public static boolean isEquippable(Inventory.Item item) {
        Optional<Obj> object = ObjectManager.getObject(item.objId);
        if (object.isPresent()) {
            Obj obj = object.get();
            return obj instanceof ObjWithClasses;
        }
        return false;
    }

    public static void use(int player, int index, Inventory.Item item) {
        Optional<Obj> object = ObjectManager.getObject(item.objId);
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
                        Log.debug("User health: " + health.min);
                        health.min = Math.min(health.min + random, health.max);
                        Log.debug("User heal: " + random + "new health: " + health.min);
                        components.add(health);
                        break;
                    case MANA:
                        Mana mana = E(player).getMana();
                        Log.debug("Prevois mana: " + mana.min);
                        mana.min = Math.min(mana.min + random, mana.max);
                        Log.debug("New mana: " + mana.min);
                        components.add(mana);
                        break;
                    case AGILITY:
                    case POISON:
                    case STRENGTH:
                        break;
                }
                // Notify update to user
                WorldManager.sendEntityUpdate(player, new EntityUpdate(player, components.toArray(new Component[0]), new Class[0]));
                // TODO remove from inventory
            }
        });
    }

    public static void equip(int player, int index, Inventory.Item item) {
        InventoryUpdate update = new InventoryUpdate();
        modifyUserEquip(player, item, index, update);
        WorldManager.sendEntityUpdate(player, update);
    }

    private static void modifyUserEquip(int player, Inventory.Item item, int index, InventoryUpdate update) {
        Optional<Obj> object = ObjectManager.getObject(item.objId);
        object.ifPresent(obj -> {
            item.equipped = !item.equipped;
            update.add(index, item);
            if (item.equipped) {
                discardItems(E(player), index, obj.getType(), update);
            }
            equipItem(player, obj, item.equipped);
        });
    }

    private static void equipItem(int player, Obj item, boolean equipped) {
        ItemConsumers.getEquipConsumer(equipped).accept(player, item);
    }

    private static void discardItems(E entity, int index, Type type, InventoryUpdate update) {
        Inventory.Item[] items = entity.getInventory().items;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && index != i) {
                int inventoryIndex = i;
                ObjectManager.getObject(items[i].objId).ifPresent(obj -> {
                    if (items[inventoryIndex].equipped && obj.getType().equals(type)) {
                        items[inventoryIndex].equipped = false;
                        update.add(inventoryIndex, items[inventoryIndex]);
                    }
                });
            }
        }
    }

    public static boolean isUsable(Inventory.Item item) {
        Optional<Obj> object = ObjectManager.getObject(item.objId);
        if (object.isPresent()) {
            return object.get().getType().equals(Type.POTION);
        }
        return false;
    }
}
