package game.systems.render.world;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.managers.MapManager;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import game.systems.render.world.WorldRenderingSystem.UserRange;
import shared.model.map.Map;
import shared.model.map.Tile;

@Wire
public class MapGroundRenderingSystem extends BaseSystem {

    public SpriteBatch batch;
    private TiledMapSystem mapSystem;
    private CameraSystem cameraSystem;
    private WorldRenderingSystem worldRenderingSystem;

    public MapGroundRenderingSystem(SpriteBatch spriteBatch) {
        this.batch = spriteBatch;
    }

    @Override
    protected void begin() {
        this.cameraSystem.camera.update();
        this.batch.setProjectionMatrix(this.cameraSystem.camera.combined);
        this.batch.begin();
    }

    private void renderWorld() {
        Map map = this.mapSystem.map;
        if (map == null) return;
        UserRange range = worldRenderingSystem.getRange(mapSystem.mapNumber);

        // LAYERS 1 & 2
        MapManager.renderLayer(map, this.batch, world.getDelta(), 0, range.minAreaX, range.maxAreaX, range.minAreaY, range.maxAreaY);
        MapManager.renderLayer(map, this.batch, world.getDelta(), 1, range.minAreaX, range.maxAreaX, range.minAreaY, range.maxAreaY);

    }

    @Override
    protected void processSystem() {
        this.renderWorld();
    }

    @Override
    protected void end() {
        this.batch.end();
    }
}
