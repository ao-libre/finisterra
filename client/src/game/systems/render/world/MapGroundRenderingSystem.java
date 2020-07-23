package game.systems.render.world;

import com.artemis.annotations.Wire;
import game.systems.map.MapManager;

@Wire(injectInherited = true)
public class MapGroundRenderingSystem extends MapLayerRenderingSystem {

    public MapGroundRenderingSystem() {
        super(MapManager.LOWER_LAYERS);
    }
}
