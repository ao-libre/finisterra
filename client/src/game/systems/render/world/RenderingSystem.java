package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;

import java.util.Comparator;

import static com.artemis.E.E;
import static game.systems.render.world.RenderingSystem.CameraKind.GUI;

@Wire
public abstract class RenderingSystem extends OrderedEntityProcessingSystem {

    private SpriteBatch batch;
    private CameraKind kind;
    @Wire
    private CameraSystem cameraSystem;

    public RenderingSystem(Aspect.Builder aspect, SpriteBatch batch, CameraKind kind) {
        super(aspect);
        this.batch = batch;
        this.kind = kind;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Camera getCamera() {
        return kind.equals(GUI) ? cameraSystem.guiCamera : cameraSystem.camera;
    }

    @Override
    protected void begin() {
        getCamera().update();
        getBatch().setProjectionMatrix(getCamera().combined);
        getBatch().begin();
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
        getBatch().end();
        doEnd();
    }

    @Override
    protected final void process(Entity e) {
        int id = e.getId();
        E fluidEntity = E(id);
        process(fluidEntity);
    }

    protected abstract void process(E e);

    enum CameraKind {
        GUI,
        WORLD
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
