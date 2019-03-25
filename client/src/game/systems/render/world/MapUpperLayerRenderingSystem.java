package game.systems.render.world;

import game.managers.MapManager;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import shared.model.map.Map;
import shared.model.map.Tile;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@Wire
public class MapUpperLayerRenderingSystem extends BaseSystem {

    public SpriteBatch batch;
    private TiledMapSystem mapSystem;
    private CameraSystem cameraSystem;

    public MapUpperLayerRenderingSystem(SpriteBatch spriteBatch) {
        this.batch = spriteBatch;
    }

    private void renderWorld() {
        // Variable Declarations
        Map map = this.mapSystem.map;
        if (map == null) return;
        int screenMinX, screenMaxX, screenMinY, screenMaxY, minAreaX, minAreaY, maxAreaX, maxAreaY;

        // Calculate visible part of the map
        int cameraPosX = (int) (this.cameraSystem.camera.position.x / Tile.TILE_PIXEL_WIDTH);
        int cameraPosY = (int) (this.cameraSystem.camera.position.y / Tile.TILE_PIXEL_HEIGHT);
        int halfWindowTileWidth = (int) ((this.cameraSystem.camera.viewportWidth / Tile.TILE_PIXEL_WIDTH) / 2f);
        int halfWindowTileHeight = (int) ((this.cameraSystem.camera.viewportHeight / Tile.TILE_PIXEL_HEIGHT) / 2f);

        screenMinX = cameraPosX - halfWindowTileWidth - 1;
        screenMaxX = cameraPosX + halfWindowTileWidth + 1;
        screenMinY = cameraPosY - halfWindowTileHeight - 1;
        screenMaxY = cameraPosY + halfWindowTileHeight + 1;

        minAreaX = screenMinX - Map.TILE_BUFFER_SIZE;
        maxAreaX = screenMaxX + Map.TILE_BUFFER_SIZE;
        minAreaY = screenMinY - Map.TILE_BUFFER_SIZE;
        maxAreaY = screenMaxY + Map.TILE_BUFFER_SIZE;

        // Make sure it is between map bounds
        if (minAreaX < Map.MIN_MAP_SIZE_WIDTH) minAreaX = Map.MIN_MAP_SIZE_WIDTH;
        if (maxAreaX > Map.MAX_MAP_SIZE_WIDTH) maxAreaX = Map.MAX_MAP_SIZE_WIDTH;
        if (minAreaY < Map.MIN_MAP_SIZE_HEIGHT) minAreaY = Map.MIN_MAP_SIZE_HEIGHT;
        if (maxAreaY > Map.MAX_MAP_SIZE_HEIGHT) maxAreaY = Map.MAX_MAP_SIZE_HEIGHT;

        // LAYER 3 - ANIMATED (POSSIBLY?)
        MapManager.renderLayer(map, this.batch, world.getDelta(), 2, minAreaX, maxAreaX, minAreaY, maxAreaY);

        // LAYER 4 - ANIMATED (POSSIBLY?)
        MapManager.renderLayer(map, this.batch, world.getDelta(), 3, minAreaX, maxAreaX, minAreaY, maxAreaY);
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
