package shared.network.lobby;

import shared.model.lobby.Player;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class JoinRoomNotification implements INotification {

    private Player player;

    public JoinRoomNotification() {}

    public JoinRoomNotification(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
