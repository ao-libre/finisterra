package ar.com.tamborindeguy.network.notifications;

import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import com.artemis.Component;

import java.util.List;

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
