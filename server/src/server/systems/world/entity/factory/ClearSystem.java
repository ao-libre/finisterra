package server.systems.world.entity.factory;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import component.entity.Clear;
import server.systems.world.WorldEntitiesSystem;

@All(Clear.class)
public class ClearSystem extends IteratingSystem {

    private WorldEntitiesSystem worldEntitiesSystem;

    ComponentMapper<Clear> mClear;

    @Override
    protected void process(int entityId) {
        Clear clear = mClear.get(entityId);
        clear.setTime(clear.getTime() - world.getDelta());
        if (clear.getTime() <= 0.0f) {
            Log.debug("Unregistering entity: " + entityId);
            worldEntitiesSystem.unregisterEntity(entityId);
        }
    }

}
