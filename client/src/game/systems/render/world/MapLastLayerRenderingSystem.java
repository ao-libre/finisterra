package game.systems.render.world;

import com.artemis.annotations.Wire;
import game.systems.map.MapManager;

@Wire(injectInherited = true)
public class MapLastLayerRenderingSystem extends MapLayerRenderingSystem {

    public MapLastLayerRenderingSystem() {
        super(MapManager.UPPER_LAYERS);
    }
}
