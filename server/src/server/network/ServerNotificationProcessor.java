package server.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import entity.character.info.Bag;
import server.systems.ServerSystem;
import server.systems.manager.ItemManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
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
        Bag bag = player.getBag();
        inventoryUpdate.getUpdates().forEach(bag::set);
    }

}
