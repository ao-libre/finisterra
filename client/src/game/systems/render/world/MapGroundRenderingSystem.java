package game.systems.render.world;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.managers.MapManager;

@Wire(injectInherited = true)
public class MapGroundRenderingSystem extends MapLayerRenderingSystem {

    public MapGroundRenderingSystem(SpriteBatch spriteBatch) {
        super(spriteBatch, MapManager.LOWER_LAYERS);
    }
}