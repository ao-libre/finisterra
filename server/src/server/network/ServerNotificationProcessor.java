package server.network;

import com.artemis.E;
import entity.character.info.Inventory;
import server.core.Server;
import shared.network.interaction.DropItem;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;

import static com.artemis.E.E;

public class ServerNotificationProcessor extends DefaultNotificationProcessor {

    private Server server;

    public ServerNotificationProcessor(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        getServer().getWorldManager().notifyToNearEntities(entityUpdate.entityId, entityUpdate);
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
            getServer().getItemManager().getItemConsumers().TAKE_OFF.accept(dropItem.getPlayerId(), getServer().getObjectManager().getObject(item.objId).get());
            item.equipped = false;
        }
        item.count -= dropItem.getCount();
        if (item.count <= 0) {
            inventory.remove(slot);
        }
        update.add(slot, inventory.items[slot]); // should remove item if count <= 0
        getServer().getNetworkManager().sendTo(getServer().getNetworkManager().getConnectionByPlayer(dropItem.getPlayerId()), update);
        // add new obj entity to world
        int object = getServer().getWorld().create();
        E(object).worldPos()
                .worldPosMap(dropItem.getPosition().map)
                .worldPosX(dropItem.getPosition().x)
                .worldPosY(dropItem.getPosition().y);
        E(object).objectIndex(item.objId);
        E(object).objectCount(dropItem.getCount());
        getServer().getWorldManager().registerEntity(object);
    }

}
