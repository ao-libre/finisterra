package ar.com.tamborindeguy.network.notifications;

import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;

public class RemoveEntity implements INotification {

    public int playerId;

    public RemoveEntity() {
    }

    public RemoveEntity(int entityId) {

        this.playerId = entityId;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
