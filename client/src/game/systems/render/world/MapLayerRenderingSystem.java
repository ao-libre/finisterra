package game.systems.render.world;

import component.camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import game.systems.resources.MapSystem;
import game.systems.map.MapManager;
import game.systems.map.TiledMapSystem;
import game.systems.render.world.WorldRenderingSystem.UserRange;
import component.position.WorldPos;
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

    public MapLayerRenderingSystem(List<Integer> layers) {
        super(Aspect.all(Focused.class));
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
            WorldPos pos = MapSystem.getHelper().getEffectivePosition(mapSystem.mapNumber, x, y);
            if (pos.map != mapSystem.mapNumber) {
                effectiveMap = MapSystem.get(pos.map);
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
            mapManager.doTileDraw(world.getDelta(), x, y, graphic);
        });
    }

    @Override
    protected void process(E e) {
        this.renderWorld();
    }
}
