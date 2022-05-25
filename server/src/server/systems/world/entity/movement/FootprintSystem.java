package server.systems.world.entity.movement;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.TimeUtils;
import component.entity.world.Footprint;
import component.position.WorldPos;
import server.systems.world.MapSystem;

import java.util.Set;

public class FootprintSystem extends IteratingSystem {

    private MapSystem mapSystem;
    private float liveTime;

    ComponentMapper<Footprint> mFootprint;

    public FootprintSystem(float liveTime) {
        super(Aspect.all(Footprint.class, WorldPos.class));
        this.liveTime = liveTime;
    }

    @Override
    protected void process(int entityId) {
        Footprint footprint = mFootprint.get(entityId);
        if (TimeUtils.millis() - footprint.getTimestamp() >= liveTime) {
            final Set<Integer> footprints = mapSystem.getEntitiesFootprints().get(footprint.getEntityId());
            if (footprints != null) {
                footprints.remove(entityId);
            }
            world.delete(entityId);
        }
    }
}
