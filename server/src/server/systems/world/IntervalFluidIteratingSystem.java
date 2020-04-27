package server.systems.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;

public class IntervalFluidIteratingSystem extends FluidIteratingSystem {

    private final float interval;
    protected float acc;

    public IntervalFluidIteratingSystem(Aspect.Builder aspect, float interval) {
        super(aspect);
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        float delta = getWorld().getDelta();
        acc += delta;
        if (acc >= interval) {
            acc -= interval;
            return true;
        }
        return false;
    }

    @Override
    protected void process(E e) {

    }
}
