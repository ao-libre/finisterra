package shared.network.lobby.player;

import shared.model.lobby.Player;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class ChangePlayerNotification implements INotification {

    private Player player;

    public ChangePlayerNotification() {
    }

    public ChangePlayerNotification(Player player) {
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
