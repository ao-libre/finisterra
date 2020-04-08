package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Camera;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;

import java.util.Comparator;

import static com.artemis.E.E;

@Wire
public abstract class RenderingSystem extends OrderedEntityProcessingSystem {

    public static final float SCALE = 2;

    private CameraSystem cameraSystem;

    public RenderingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    public Camera getCamera() {
        return cameraSystem.camera;
    }

    @Override
    protected void begin() {
        getCamera().update();
        doBegin();
    }

    /*
     * Extending systems should implement this for extra begin actions
     */
    protected void doBegin() {
    }

    /*
     * Extending systems should implement this for extra end actions
     */
    protected void doEnd() {
    }

    @Override
    protected void end() {
        doEnd();
    }

    @Override
    protected final void process(Entity e) {
        int id = e.getId();
        E fluidEntity = E(id);
        process(fluidEntity);
    }

    protected abstract void process(E e);

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }

}
