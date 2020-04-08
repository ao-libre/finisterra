package game.systems.world;

import com.artemis.E;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import component.entity.Clear;

@All({Clear.class})
@Wire
public class ClearSystem extends IteratingSystem {

    private NetworkedEntitySystem entitySystem;

    @Override
    protected void process(int entityId) {
        E e = E.E(entityId);
        e.getClear().setTime(e.getClear().getTime() - world.getDelta());
        if (e.clearTime() <= 0) {
            Log.debug("Unregistering entity: " + entityId);
            if (entitySystem.existsLocal(entityId)) {
                entitySystem.unregisterLocalEntity(entityId);
            } else {
                e.deleteFromWorld();
            }
        }
    }

}
