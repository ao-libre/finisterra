package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.TimeUtils;
import entity.world.Footprint;
import position.WorldPos;
import server.core.Server;

import java.util.Set;

public class FootprintSystem extends IteratingSystem {

    private Server server;
    private float liveTime;

    public FootprintSystem(Server server, float liveTime) {
        super(Aspect.all(Footprint.class, WorldPos.class));
        this.server = server;
        this.liveTime = liveTime;
    }

    @Override
    protected void process(int entityId) {
        final E e = E.E(entityId);
        if (TimeUtils.millis() - e.footprintTimestamp() >= liveTime) {
            final Set<Integer> footprints = server.getMapManager().getEntitiesFootprints().get(e.footprintEntityId());
            if (footprints != null) {
                footprints.remove(e.id());
            }
            e.deleteFromWorld();
        }
    }
}
