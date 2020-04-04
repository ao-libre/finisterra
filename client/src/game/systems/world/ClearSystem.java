package game.systems.world;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.entity.Clear;

@Wire
public class ClearSystem extends IteratingSystem {

    private NetworkedEntitySystem entitySystem;

    public ClearSystem() {
        super(Aspect.all(Clear.class));
    }

    @Override
    protected void process(int entityId) {
        entitySystem.unregisterLocalEntity(entityId);
    }

}
