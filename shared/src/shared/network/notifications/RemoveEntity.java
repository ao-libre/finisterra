package shared.network.notifications;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

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
