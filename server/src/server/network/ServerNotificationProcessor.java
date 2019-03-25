package server.network;

import server.core.WorldServer;
import server.manager.ObjectManager;
import server.manager.WorldManager;
import shared.network.interaction.DropItem;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.network.notifications.RemoveEntity;
import com.artemis.E;
import entity.character.info.Inventory;

import static server.manager.ItemConsumers.TAKE_OFF;
import static com.artemis.E.E;

public class ServerNotificationProcessor implements INotificationProcessor {
    @Override
    public void defaultProcess(INotification notification) {
    }

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        WorldManager.notifyToNearEntities(entityUpdate.entityId, entityUpdate);
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
        if (item == null) {
            return;
        }
        if (item.equipped) {
            TAKE_OFF.accept(dropItem.getPlayerId(), ObjectManager.getObject(item.objId).get());
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

    @Override
    public void processNotification(MovementNotification movementNotification) {
        defaultProcess(movementNotification);
    }

    @Override
    public void processNotification(FXNotification fxNotification) {
        defaultProcess(fxNotification);
    }
}
