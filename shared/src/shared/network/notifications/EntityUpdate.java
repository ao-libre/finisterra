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

    public static class EntityUpdateBuilder {

        private EntityUpdate entityUpdate;

        public static EntityUpdateBuilder of(int entityId) {
            EntityUpdateBuilder builder = new EntityUpdateBuilder();
            builder.entityUpdate = new EntityUpdate();
            builder.entityUpdate.entityId = entityId;

            return builder;
        }

        public EntityUpdateBuilder withComponents(Component... components){
            entityUpdate.components = components;
            return this;
        }

        public EntityUpdateBuilder remove(Class... toRemove) {
            entityUpdate.toRemove = toRemove;
            return this;
        }

        public EntityUpdate build() {
            if (entityUpdate.toRemove == null) {
                entityUpdate.toRemove = new Class[0];
            }
            if (entityUpdate.components == null) {
                entityUpdate.components = new Component[0];
            }
            return entityUpdate;
        }
    }



    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
