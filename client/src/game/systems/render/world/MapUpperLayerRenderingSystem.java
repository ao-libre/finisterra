package game.systems.render.world;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.managers.MapManager;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import shared.model.map.Map;

@Wire
public class MapUpperLayerRenderingSystem extends BaseSystem {

    public SpriteBatch batch;
    private TiledMapSystem mapSystem;
    private CameraSystem cameraSystem;
    private WorldRenderingSystem worldRenderingSystem;

    public MapUpperLayerRenderingSystem(SpriteBatch spriteBatch) {
        this.batch = spriteBatch;
    }

    private void renderWorld() {
        // Variable Declarations
        Map map = this.mapSystem.map;
        if (map == null) return;
        WorldRenderingSystem.UserRange range = worldRenderingSystem.getRange();
        // LAYER 4 - ANIMATED (POSSIBLY?)
        MapManager.renderLayer(map, this.batch, world.getDelta(), 3, range.minAreaX, range.maxAreaX, range.minAreaY, range.maxAreaY);
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(this.cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void processSystem() {
        renderWorld();
    }

    @Override
    protected void end() {
        batch.end();
    }
}
