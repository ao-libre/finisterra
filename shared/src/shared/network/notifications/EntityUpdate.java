package shared.network.notifications;

import com.artemis.Component;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class EntityUpdate implements INotification {

    public int entityId;
    public Component[] components;
    public Class[] toRemove;

    public EntityUpdate() {
    }

    public EntityUpdate(int entityId, Component[] components, Class[] toRemove) {
        this.entityId = entityId;
        this.components = components;
        this.toRemove = toRemove;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
