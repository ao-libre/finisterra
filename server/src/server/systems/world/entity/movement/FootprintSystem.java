package server.systems.world.entity.movement;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.TimeUtils;
import component.entity.world.Footprint;
import component.position.WorldPos;
import server.systems.world.MapSystem;

import java.util.Set;

@Wire
public class FootprintSystem extends IteratingSystem {

    private MapSystem mapSystem;
    private float liveTime;

    public FootprintSystem(float liveTime) {
        super(Aspect.all(Footprint.class, WorldPos.class));
        this.liveTime = liveTime;
    }

    @Override
    protected void process(int entityId) {
        final E footprint = E.E(entityId);
        if (TimeUtils.millis() - footprint.footprintTimestamp() >= liveTime) {
            final Set<Integer> footprints = mapSystem.getEntitiesFootprints().get(footprint.footprintEntityId());
            if (footprints != null) {
                footprints.remove(footprint.id());
            }
            footprint.deleteFromWorld();
        }
    }
}
