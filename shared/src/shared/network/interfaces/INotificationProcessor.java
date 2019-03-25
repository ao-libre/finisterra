package shared.network.interfaces;

import shared.network.interaction.DropItem;
import shared.network.inventory.InventoryUpdate;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.network.notifications.RemoveEntity;

public interface INotificationProcessor {

    void defaultProcess(INotification notification);

    void processNotification(EntityUpdate notification);

    void processNotification(RemoveEntity removeEntity);

    void processNotification(InventoryUpdate inventoryUpdate);

    void processNotification(DropItem dropItem);

    void processNotification(MovementNotification movementNotification);

    void processNotification(FXNotification fxNotification);
}
