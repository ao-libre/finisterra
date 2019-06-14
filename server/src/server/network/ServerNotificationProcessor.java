package server.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import entity.character.info.Inventory;
import server.systems.ServerSystem;
import server.systems.manager.ItemManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
import shared.network.interaction.DropItem;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;

import static com.artemis.E.E;

@Wire
public class ServerNotificationProcessor extends DefaultNotificationProcessor {

    private WorldManager worldManager;
    private ItemManager itemManager;
    private ObjectManager objectManager;
    private ServerSystem networkManager;

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        worldManager.notifyToNearEntities(entityUpdate.entityId, entityUpdate);
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
            itemManager.getItemConsumers().TAKE_OFF.accept(dropItem.getPlayerId(), objectManager.getObject(item.objId).get());
            item.equipped = false;
        }
        item.count -= dropItem.getCount();
        if (item.count <= 0) {
            inventory.remove(slot);
        }
        update.add(slot, inventory.items[slot]); // should remove item if count <= 0
        networkManager.sendTo(networkManager.getConnectionByPlayer(dropItem.getPlayerId()), update);
        // add new obj entity to world
        int object = world.create();
        E(object).worldPos()
                .worldPosMap(dropItem.getPosition().map)
                .worldPosX(dropItem.getPosition().x)
                .worldPosY(dropItem.getPosition().y);
        E(object).objectIndex(item.objId);
        E(object).objectCount(dropItem.getCount());
        worldManager.registerEntity(object);
    }

}
