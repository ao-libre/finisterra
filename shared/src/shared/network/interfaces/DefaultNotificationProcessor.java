package shared.network.interfaces;

import com.artemis.BaseSystem;
import shared.network.battle.DominationNotification;
import shared.network.interaction.DropItem;
import shared.network.inventory.InventoryUpdate;
import shared.network.lobby.JoinRoomNotification;
import shared.network.lobby.NewRoomNotification;
import shared.network.lobby.player.ChangePlayerNotification;
import shared.network.movement.MovementNotification;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;
import shared.network.sound.SoundNotification;

public class DefaultNotificationProcessor extends BaseSystem implements INotificationProcessor {

    @Override
    public void defaultProcess(INotification notification) {

    }

    @Override
    public void processNotification(EntityUpdate notification) {

    }

    @Override
    public void processNotification(RemoveEntity removeEntity) {

    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {

    }

    @Override
    public void processNotification(DropItem dropItem) {

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
    public void processNotification(ConsoleMessage consoleMessage) {

    }

    @Override
    public void processNotification(SoundNotification soundNotification) {

    }

    @Override
    public void processNotification(ChangePlayerNotification changePlayerNotification) {

    }

    @Override
    public void processNotification(DominationNotification dominationNotification) {

    }

    @Override
    protected void processSystem() {

    }
}
