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
public class MapUpperLayerRenderingSystem extends BaseSystem {

    public SpriteBatch batch;
    private TiledMapSystem mapSystem;
    private CameraSystem cameraSystem;
    private WorldRenderingSystem worldRenderingSystem;

    public MapUpperLayerRenderingSystem(SpriteBatch spriteBatch) {
        this.batch = spriteBatch;
    }

    private void renderWorld() {
        final Map map = this.mapSystem.map;
        if (map == null) return;
        UserRange range = worldRenderingSystem.getRange();

        drawRange(map, range);
    }

    private void drawRange(Map map, UserRange range) {
        range.forEachTile((x, y) -> {
            Map effectiveMap = map;
            WorldPos pos = MapHandler.getHelper().getEffectivePosition(mapSystem.mapNumber, x, y);
            if (pos.map != mapSystem.mapNumber) {
                effectiveMap = MapHandler.get(pos.map);
            }
            drawGraphicInLayer(3, x, y, effectiveMap, pos);
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
