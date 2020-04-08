package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import component.entity.Clear;
import server.systems.manager.WorldManager;

import static com.artemis.E.E;

@Wire
public class ClearSystem extends IteratingSystem {

    private WorldManager worldManager;

    public ClearSystem() {
        super(Aspect.all(Clear.class));
    }

    @Override
    protected void process(int entityId) {
        E e = E(entityId);
        e.getClear().setTime(e.getClear().getTime() - world.getDelta());
        if (e.clearTime() <= 0) {
            Log.debug("Unregistering entity: " + entityId);
            worldManager.unregisterEntity(entityId);
        }
    }

}
