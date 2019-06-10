package game.systems.render.world;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.managers.MapManager;

@Wire(injectInherited = true)
public class MapLastLayerRenderingSystem extends MapLayerRenderingSystem {

    public MapLastLayerRenderingSystem(SpriteBatch spriteBatch) {
        super(spriteBatch, MapManager.UPPER_LAYERS);
    }
}
