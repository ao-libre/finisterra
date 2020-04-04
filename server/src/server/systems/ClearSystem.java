package server.systems;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import component.entity.Clear;
import server.systems.manager.WorldManager;

@Wire
public class ClearSystem extends IteratingSystem {

    private WorldManager worldManager;

    public ClearSystem() {
        super(Aspect.all(Clear.class));
    }

    @Override
    protected void process(int entityId) {
        Log.debug("Unregistering entity: " + entityId);
        worldManager.unregisterEntity(entityId);
    }

}
