package shared.network.notifications;

import com.artemis.Component;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

import java.util.Arrays;

public class EntityUpdate implements INotification {

    public static final int NO_ENTITY = -1;

    public int entityId;
    public Component[] components;
    public Class<? extends Component>[] toRemove;

    public EntityUpdate() {
    }

    public EntityUpdate(int entityId) {
        this.entityId = entityId;
    }

    public EntityUpdate(int entityId, Component[] components, Class<? extends Component>[] toRemove) {
        this.entityId = entityId;
        this.components = components;
        this.toRemove = toRemove;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }

    @Override
    public String toString() {
        return "EntityUpdate{" +
                "entityId=" + entityId +
                ", components=" + Arrays.toString(components) +
                ", toRemove=" + Arrays.toString(toRemove) +
                '}';
    }
}
