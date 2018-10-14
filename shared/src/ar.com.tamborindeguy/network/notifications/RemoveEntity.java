package ar.com.tamborindeguy.network.notifications;

import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;

public class RemoveEntity implements INotification {

    public int entityId;

    public RemoveEntity() {
    }

    public RemoveEntity(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
