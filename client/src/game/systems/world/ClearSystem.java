package game.systems.world;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import component.entity.Clear;

@All(Clear.class)
public class ClearSystem extends IteratingSystem {

    private NetworkedEntitySystem entitySystem;

    ComponentMapper<Clear> mClear;

    @Override
    protected void process(int entityId) {
        Clear clear = mClear.get(entityId);
        clear.setTime(clear.getTime() - world.getDelta());
        if (clear.getTime() <= 0.0f) {
            Log.debug("Unregistering entity: " + entityId);
            if (entitySystem.existsLocal(entityId)) {
                entitySystem.unregisterLocalEntity(entityId);
            } else {
                world.getEntity(entityId).deleteFromWorld();
            }
        }
    }

}
