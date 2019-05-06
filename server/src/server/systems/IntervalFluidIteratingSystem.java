package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;

public class IntervalFluidIteratingSystem extends FluidIteratingSystem {

    protected float acc;
    private final float interval;

    public IntervalFluidIteratingSystem(Aspect.Builder aspect, float interval) {
        super(aspect);
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        float delta = getWorld().getDelta();
        System.out.println(delta);
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
