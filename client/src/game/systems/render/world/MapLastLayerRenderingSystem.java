package game.systems.render.world;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import game.managers.MapManager;

@Wire(injectInherited = true)
public class MapLastLayerRenderingSystem extends MapLayerRenderingSystem {

    public MapLastLayerRenderingSystem(Batch spriteBatch) {
        super(spriteBatch, MapManager.UPPER_LAYERS);
    }
}
