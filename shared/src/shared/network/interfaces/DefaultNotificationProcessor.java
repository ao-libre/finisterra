package shared.network.interfaces;

import com.artemis.BaseSystem;
import shared.network.inventory.InventoryUpdate;
import shared.network.lobby.JoinRoomNotification;
import shared.network.lobby.NewRoomNotification;
import shared.network.lobby.player.ChangePlayerNotification;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;

public class DefaultNotificationProcessor extends BaseSystem implements INotificationProcessor {

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

    @Override
    public void processNotification(JoinRoomNotification joinRoomNotification) {

    }

    @Override
    public void processNotification(NewRoomNotification newRoomNotification) {

    }

    @Override
    public void processNotification(ChangePlayerNotification changePlayerNotification) {

    }

    @Override
    protected void processSystem() {

    }
}
