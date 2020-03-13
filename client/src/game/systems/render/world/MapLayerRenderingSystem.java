package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import game.handlers.MapHandler;
import game.managers.MapManager;
import game.systems.map.TiledMapSystem;
import game.systems.render.world.WorldRenderingSystem.UserRange;
import position.WorldPos;
import shared.model.map.Map;
import shared.util.MapHelper;

import java.util.List;
import java.util.Optional;

@Wire(injectInherited = true)
public class MapLayerRenderingSystem extends RenderingSystem {

    private final List<Integer> layers;
    private MapManager mapManager;
    private TiledMapSystem mapSystem;
    private WorldRenderingSystem worldRenderingSystem;

    public MapLayerRenderingSystem(Batch spriteBatch, List<Integer> layers) {
        super(Aspect.all(Focused.class), spriteBatch, CameraKind.WORLD);
        this.layers = layers;
    }

    private void renderWorld() {
        final Map map = this.mapSystem.map;
        if (map == null) return;
        doRender(map);
    }

    protected void doRender(Map map) {
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
            Map finalEffectiveMap = effectiveMap;
            layers.forEach(layer -> drawGraphicInLayer(layer, x, y, finalEffectiveMap, pos));
        });
    }

    private void drawGraphicInLayer(int layer, int x, int y, Map map, WorldPos pos) {
        Optional.ofNullable(MapHelper.getTile(map, pos)).ifPresent(tile -> {
            int graphic = tile.getGraphic(layer);
            if (graphic == 0) {
                return;
            }
            mapManager.doTileDraw(getBatch(), world.getDelta(), x, y, graphic);
        });
    }

    @Override
    protected void process(E e) {
        this.renderWorld();
    }
}
