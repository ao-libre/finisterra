package shared.network.interfaces;

import shared.network.inventory.InventoryUpdate;
import shared.network.lobby.JoinRoomNotification;
import shared.network.lobby.NewRoomNotification;
import shared.network.lobby.player.ChangePlayerNotification;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;

public interface INotificationProcessor {

    void defaultProcess(INotification notification);

    void processNotification(EntityUpdate notification);

    void processNotification(InventoryUpdate inventoryUpdate);

    void processNotification(MovementNotification movementNotification);

    void processNotification(JoinRoomNotification joinRoomNotification);

    void processNotification(NewRoomNotification newRoomNotification);

    void processNotification(ChangePlayerNotification changePlayerNotification);
}
