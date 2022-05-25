package server.systems.network;

import com.artemis.ComponentMapper;
import component.entity.character.info.Bag;
import server.systems.config.ObjectSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.item.ItemSystem;
import server.utils.UpdateTo;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;

public class ServerNotificationProcessor extends DefaultNotificationProcessor {

    private WorldEntitiesSystem worldEntitiesSystem;
    private ItemSystem itemSystem;
    private ObjectSystem objectSystem;
    private ServerSystem networkManager;
    private EntityUpdateSystem entityUpdateSystem;

    ComponentMapper<Bag> mBag;

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        entityUpdateSystem.add(entityUpdate, UpdateTo.NEAR);
    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        int entityId = inventoryUpdate.getId();
        Bag bag = mBag.create(entityId);
        inventoryUpdate.getUpdates().forEach(bag::set);
    }

}
