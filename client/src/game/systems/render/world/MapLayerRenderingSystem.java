package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.handlers.MapHandler;
import game.managers.MapManager;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import game.systems.render.world.WorldRenderingSystem.UserRange;
import position.WorldPos;
import shared.model.map.Map;

import java.util.List;
import java.util.Optional;

@Wire(injectInherited = true)
public class MapLayerRenderingSystem extends RenderingSystem {

    private TiledMapSystem mapSystem;
    private CameraSystem cameraSystem;
    private WorldRenderingSystem worldRenderingSystem;
    private List<Integer> layers;

    public MapLayerRenderingSystem(SpriteBatch spriteBatch, List<Integer> layers) {
        super(Aspect.all(Focused.class), spriteBatch, CameraKind.WORLD);
        this.layers = layers;
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
            Map finalEffectiveMap = effectiveMap;
            layers.forEach(layer -> drawGraphicInLayer(layer, x, y, finalEffectiveMap, pos));
        });
    }

    private void drawGraphicInLayer(int layer, int x, int y, Map map, WorldPos pos) {
        Optional.ofNullable(MapHandler.getHelper().getTile(map, pos)).ifPresent(tile -> {
            int graphic = tile.getGraphic(layer);
            if (graphic == 0) {
                return;
            }
            MapManager.doTileDraw(getBatch(), world.getDelta(), y, x, graphic);
        });
    }

    @Override
    protected void process(E e) {
        this.renderWorld();
    }
}
