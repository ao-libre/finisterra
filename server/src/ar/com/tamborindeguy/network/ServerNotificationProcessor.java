package ar.com.tamborindeguy.network;

import ar.com.tamborindeguy.core.WorldServer;
import ar.com.tamborindeguy.manager.ItemConsumers;
import ar.com.tamborindeguy.manager.ObjectManager;
import ar.com.tamborindeguy.manager.WorldManager;
import ar.com.tamborindeguy.network.interaction.DropItem;
import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import com.artemis.E;
import entity.character.info.Inventory;

import static com.artemis.E.E;

public class ServerNotificationProcessor implements INotificationProcessor {
    @Override
    public void defaultProcess(INotification notification) {
    }

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        WorldManager.notifyUpdateToNearEntities(entityUpdate);
    }

    @Override
    public void processNotification(RemoveEntity removeEntity) {
        defaultProcess(removeEntity);
    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        E player = E(inventoryUpdate.getId());
        Inventory inventory = player.getInventory();
        inventoryUpdate.getUpdates().forEach(inventory::set);
    }

    @Override
    public void processNotification(DropItem dropItem) {
        int slot = dropItem.getSlot();
        E entity = E(dropItem.getPlayerId());
        // remove item from inventory
        InventoryUpdate update = new InventoryUpdate();
        Inventory inventory = entity.getInventory();
        Inventory.Item item = inventory.items[slot];
        if (item.equipped) {
            ItemConsumers.getEquipConsumer(false).accept(dropItem.getPlayerId(), ObjectManager.getObject(item.objId).get());
            item.equipped = false;
        }
        item.count -= dropItem.getCount();
        if (item.count <= 0) {
            inventory.remove(slot);
        }
        update.add(slot, inventory.items[slot]); // should remove item if count <= 0
        NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(dropItem.getPlayerId()), update);
        // add new obj entity to world
        int object = WorldServer.getWorld().create();
        E(object).worldPos()
                .worldPosMap(dropItem.getPosition().map)
                .worldPosX(dropItem.getPosition().x)
                .worldPosY(dropItem.getPosition().y);
        E(object).objectIndex(item.objId);
        E(object).objectCount(dropItem.getCount());
        WorldManager.registerItem(object);
    }
}
