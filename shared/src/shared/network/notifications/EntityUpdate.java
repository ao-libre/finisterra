package shared.network.notifications;

import com.artemis.Component;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

import java.util.HashSet;
import java.util.Set;

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

    public static class EntityUpdateBuilder {

        private EntityUpdate entityUpdate;
        private Set<Component> components = new HashSet<>();
        private Set<Class> toRemove = new HashSet<>();

        public static EntityUpdateBuilder of(int entityId) {
            EntityUpdateBuilder builder = new EntityUpdateBuilder();
            builder.entityUpdate = new EntityUpdate();
            builder.entityUpdate.entityId = entityId;
            return builder;
        }

        public EntityUpdateBuilder withComponents(Component... components) {
            for (Component component : components) {
                this.components.add(component);
            }
            return this;
        }

        public EntityUpdateBuilder remove(Class... toRemove) {
            for (Class clazz : toRemove) {
                this.toRemove.add(clazz);
            }
            return this;
        }

        public EntityUpdate build() {
            entityUpdate.components = components.toArray(new Component[0]);
            entityUpdate.toRemove = toRemove.toArray(new Class[0]);
            return entityUpdate;
        }
    }
}
