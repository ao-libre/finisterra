package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.ObjWithClasses;
import ar.com.tamborindeguy.objects.types.Type;
import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import entity.Object;
import entity.character.info.Inventory;

import java.util.Optional;

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

    }

    public static void equip(int player, int index, Inventory.Item item) {
        InventoryUpdate update = new InventoryUpdate();
        modifyUserEquip(player, item, index, update);
        NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(player), update);
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
        return false;
    }
}
