package shared.network.interfaces;

import shared.network.inventory.InventoryUpdate;
import shared.network.lobby.JoinRoomNotification;
import shared.network.lobby.NewRoomNotification;
import shared.network.lobby.player.ChangePlayerNotification;
import shared.network.movement.MovementNotification;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;
import shared.network.sound.SoundNotification;

public interface INotificationProcessor {

    void defaultProcess(INotification notification);

    void processNotification(EntityUpdate notification);

    void processNotification(RemoveEntity removeEntity);

    void processNotification(InventoryUpdate inventoryUpdate);

    void processNotification(MovementNotification movementNotification);

    void processNotification(JoinRoomNotification joinRoomNotification);

    void processNotification(NewRoomNotification newRoomNotification);

    void processNotification(ConsoleMessage consoleMessage);

    void processNotification(SoundNotification soundNotification);

    void processNotification(ChangePlayerNotification changePlayerNotification);
}
