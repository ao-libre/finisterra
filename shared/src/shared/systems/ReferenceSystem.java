package shared.systems;

import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.entity.Ref;

import static com.artemis.E.E;

@Wire
@All({Ref.class})
public abstract class ReferenceSystem extends IteratingSystem {

    @Override
    protected abstract void inserted(int entityId);

    @Override
    protected void process(int entityId) {
        int refId = E(entityId).refId();

        if (E(refId) == null || !world.getEntity(refId).isActive()) {
            delete(entityId);
        }
    }

    protected void delete(int entityId) {
        E(entityId).deleteFromWorld();
    }
}
