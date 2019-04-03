package object.systems;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import game.utils.WorldUtils;
import physics.AOPhysics;

import static com.artemis.E.E;

public class FaceChangerSystem extends IteratingSystem {

    public FaceChangerSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class));
    }

    @Override
    protected void process(int entityId) {
        E(entityId).getAOPhysics().getMovementIntention().ifPresent(mov -> E(entityId).headingCurrent(WorldUtils.getHeading(mov)));
    }
}
