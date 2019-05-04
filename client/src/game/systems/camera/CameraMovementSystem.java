package game.systems.camera;

import camera.AOCamera;
import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import position.Pos2D;

import static com.artemis.E.E;

@Wire
public class CameraMovementSystem extends IteratingSystem {

    private CameraSystem cameraSystem;

    /**
     * Creates a new CameraMovementSystem.
     */
    public CameraMovementSystem() {
        super(Aspect.all(Pos2D.class, AOCamera.class));
    }

    @Override
    protected void process(int camera) {
        final Pos2D pos = E(camera).getPos2D();

        cameraSystem.camera.position.x = (pos.x);
        cameraSystem.camera.position.y = (pos.y);
        cameraSystem.camera.update();
    }

}
