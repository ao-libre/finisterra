package shared.network.interfaces;

import shared.network.inventory.InventoryUpdate;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;

public interface INotificationProcessor {

    void defaultProcess(INotification notification);

    void processNotification(EntityUpdate notification);

    void processNotification(InventoryUpdate inventoryUpdate);

    void processNotification(MovementNotification movementNotification);

}
