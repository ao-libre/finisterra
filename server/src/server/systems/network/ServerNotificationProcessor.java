package server.systems.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.info.Bag;
import server.systems.config.ObjectSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.item.ItemSystem;
import server.utils.UpdateTo;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;

import static com.artemis.E.E;

@Wire
public class ServerNotificationProcessor extends DefaultNotificationProcessor {

    private WorldEntitiesSystem worldEntitiesSystem;
    private ItemSystem itemSystem;
    private ObjectSystem objectSystem;
    private ServerSystem networkManager;
    private EntityUpdateSystem entityUpdateSystem;

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        entityUpdateSystem.add(entityUpdate, UpdateTo.NEAR);
    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        E player = E(inventoryUpdate.getId());
        Bag bag = player.getBag();
        inventoryUpdate.getUpdates().forEach(bag::set);
    }

}
