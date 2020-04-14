package shared.network.interfaces;

import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.network.inventory.InventoryUpdate;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;

public class DefaultNotificationProcessor extends PassiveSystem implements INotificationProcessor {

    @Override
    public void defaultProcess(INotification notification) {

    }

    @Override
    public void processNotification(EntityUpdate notification) {

    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {

    }

    @Override
    public void processNotification(MovementNotification movementNotification) {

    }


}
