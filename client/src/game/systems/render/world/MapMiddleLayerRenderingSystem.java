package game.systems.render.world;

import game.systems.map.MapManager;

public class MapMiddleLayerRenderingSystem extends MapLayerRenderingSystem {

    public MapMiddleLayerRenderingSystem() {
        super( MapManager.MIDDLE_LAYERS);

    }
}
