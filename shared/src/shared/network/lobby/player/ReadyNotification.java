package shared.network.lobby.player;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class ReadyNotification implements INotification {

    private boolean ready;

    public ReadyNotification() {}

    public ReadyNotification(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
