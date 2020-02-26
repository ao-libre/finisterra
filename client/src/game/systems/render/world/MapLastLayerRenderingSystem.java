package game.systems.render.world;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureArraySpriteBatch;
import game.managers.MapManager;

@Wire(injectInherited = true)
public class MapLastLayerRenderingSystem extends MapLayerRenderingSystem {

    public MapLastLayerRenderingSystem(TextureArraySpriteBatch spriteBatch) {
        super(spriteBatch, MapManager.UPPER_LAYERS);
    }
}
