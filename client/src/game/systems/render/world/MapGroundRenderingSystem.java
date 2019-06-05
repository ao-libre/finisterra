package game.systems.render.world;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.handlers.MapHandler;
import game.managers.MapManager;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import game.systems.render.world.WorldRenderingSystem.UserRange;
import position.WorldPos;
import shared.model.map.Map;

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
        final Map map = this.mapSystem.map;
        if (map == null) return;
        UserRange range = worldRenderingSystem.getRange();

        // LAYERS 1 & 2
        drawRange(map, range);
    }

    private void drawRange(Map map, UserRange range) {
        range.forEachTile((x, y) -> {
            Map effectiveMap = map;
            WorldPos pos = MapHandler.getHelper().getEffectivePosition(mapSystem.mapNumber, x, y);
            if (pos.map != mapSystem.mapNumber) {
                effectiveMap = MapHandler.get(pos.map);
            }
            drawGraphicInLayer(0, x, y, effectiveMap, pos);
            drawGraphicInLayer(1, x, y, effectiveMap, pos);
        });
    }

    private void drawGraphicInLayer(int layer, int x, int y, Map effectiveMap, WorldPos pos) {
        int graphic = effectiveMap.getTile(pos.x, pos.y).getGraphic(layer);
        if (graphic == 0) {
            return;
        }
        MapManager.doTileDraw(this.batch, world.getDelta(), y, x, graphic);
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
