package shared.network.lobby;

import shared.model.lobby.Player;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class JoinRoomNotification implements INotification {

    private Player player;

    private boolean enter; // or exit

    public JoinRoomNotification() {
    }

    public JoinRoomNotification(Player player, boolean enter) {
        this.player = player;
        this.enter = enter;
    }

    public Player getPlayer() {
        return player;
    }


    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }

    public boolean isEnter() {
        return enter;
    }
}
