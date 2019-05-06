package shared.network.interfaces;

import shared.network.interaction.DropItem;
import shared.network.inventory.InventoryUpdate;
import shared.network.lobby.JoinRoomNotification;
import shared.network.lobby.NewRoomNotification;
import shared.network.lobby.player.ChangeHeroNotification;
import shared.network.lobby.player.ChangeTeamNotification;
import shared.network.lobby.player.ReadyNotification;
import shared.network.movement.MovementNotification;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.network.notifications.RemoveEntity;
import shared.network.sound.SoundNotification;

public interface INotificationProcessor {

    void defaultProcess(INotification notification);

    void processNotification(EntityUpdate notification);

    void processNotification(RemoveEntity removeEntity);

    void processNotification(InventoryUpdate inventoryUpdate);

    void processNotification(DropItem dropItem);

    void processNotification(MovementNotification movementNotification);

    void processNotification(FXNotification fxNotification);

    void processNotification(JoinRoomNotification joinRoomNotification);

    void processNotification(NewRoomNotification newRoomNotification);

    void processNotification(ChangeTeamNotification changeTeamNotification);

    void processNotification(ChangeHeroNotification changeHeroNotification);

    void processNotification(ReadyNotification readyNotification);

    void processNotification(ConsoleMessage consoleMessage);

    void processNotification(SoundNotification soundNotification);
}
