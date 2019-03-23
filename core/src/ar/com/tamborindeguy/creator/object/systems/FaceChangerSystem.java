package ar.com.tamborindeguy.creator.object.systems;

import ar.com.tamborindeguy.client.utils.WorldUtils;
import camera.Focused;
import com.artemis.ArtemisPlugin;
import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.systems.IteratingSystem;
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
