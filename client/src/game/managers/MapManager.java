package game.managers;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.handlers.AnimationHandler;
import model.textures.BundledAnimation;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapManager extends BaseSystem {

    private AnimationHandler animationHandler;

    public static final List<Integer> LOWER_LAYERS = Arrays.asList(0, 1);
    public static final List<Integer> UPPER_LAYERS = Collections.singletonList(3);

    public static final int TILE_BUFFER_SIZE = 7;

    public static final int MAX_MAP_SIZE_WIDTH = 100;
    public static final int MIN_MAP_SIZE_WIDTH = 1;
    public static final int MAX_MAP_SIZE_HEIGHT = 100;
    public static final int MIN_MAP_SIZE_HEIGHT = 1;

    public void drawTile(Map map, SpriteBatch batch, float delta, int layer, int y, int x) {
        int graphic = map.getTile(x, y).getGraphic(layer);
        if (graphic == 0) {
            return;
        }

        doTileDraw(batch, delta, y, x, graphic);
    }

    public void doTileDraw(SpriteBatch batch, float delta, int y, int x, int graphic) {
        BundledAnimation animation = animationHandler.getGraphicAnimation(graphic);
        TextureRegion tileRegion = animation.isAnimated() ? animation.getAnimatedGraphic(true) : animation.getGraphic();

        if (animation.isAnimated()) {
            animation.setAnimationTime(animation.getAnimationTime() + delta);
        }

        doTileDraw(batch, y, x, tileRegion);
    }

    public void doTileDraw(SpriteBatch batch, int y, int x, TextureRegion tileRegion) {
        if (tileRegion != null) {
            final float mapPosX = (x * Tile.TILE_PIXEL_WIDTH);
            final float mapPosY = (y * Tile.TILE_PIXEL_HEIGHT);
            final float tileOffsetX = mapPosX + (Tile.TILE_PIXEL_WIDTH - tileRegion.getRegionWidth()) / 2;
            final float tileOffsetY = mapPosY - tileRegion.getRegionHeight() + Tile.TILE_PIXEL_HEIGHT;
            batch.draw(tileRegion, tileOffsetX, tileOffsetY);
        }
    }


    @Override
    protected void processSystem() {}
}
