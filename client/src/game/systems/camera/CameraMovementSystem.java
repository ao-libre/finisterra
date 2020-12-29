package game.systems.camera;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import component.camera.AOCamera;
import component.position.WorldPosOffsets;
import game.systems.render.BatchSystem;
import shared.model.map.Tile;

import static com.artemis.E.E;

@Wire
public class CameraMovementSystem extends IteratingSystem {

    private CameraSystem cameraSystem;
    private BatchSystem batchSystem;

    /**
     * Creates a new CameraMovementSystem.
     */
    public CameraMovementSystem() {
        super(Aspect.all(WorldPosOffsets.class, AOCamera.class));
    }

    @Override
    protected void process(int cameraEntity) {
        final WorldPosOffsets pos = E(cameraEntity).getWorldPosOffsets();

        OrthographicCamera camera = cameraSystem.camera;
        camera.position.x = pos.x;
        camera.position.y = pos.y;
        camera.position.x += Tile.TILE_PIXEL_WIDTH / 2;
        camera.position.y -= Tile.TILE_PIXEL_HEIGHT;
        camera.update();

        batchSystem.getBatch().setProjectionMatrix(camera.combined);
    }

}
