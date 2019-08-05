package game.managers;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.handlers.AnimationHandler;
import model.textures.AOTexture;
import model.textures.BundledAnimation;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapManager extends BaseSystem {

    public static final List<Integer> LOWER_LAYERS = Arrays.asList(0, 1);
    public static final List<Integer> UPPER_LAYERS = Collections.singletonList(3);
    public static final int TILE_BUFFER_SIZE = 7;
    public static final int MAX_MAP_SIZE_WIDTH = 100;
    public static final int MIN_MAP_SIZE_WIDTH = 1;
    public static final int MAX_MAP_SIZE_HEIGHT = 100;
    public static final int MIN_MAP_SIZE_HEIGHT = 1;
    private AnimationHandler animationHandler;

    public void drawLayer(Map map, SpriteBatch batch, float delta, int layer, boolean drawExit, boolean drawBlock) {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = map.getHeight() - 1; y >= 0; y--) {
                Tile tile = map.getTile(x, y);
                if (tile == null) {
                    continue;
                }
                int graphic = tile.getGraphic(layer);
                if (graphic == 0) {
                    continue;
                }
                doTileDrawFlipped(batch, delta, x, y, graphic);
                if (drawBlock && tile.isBlocked()) {
                    // draw block
                    doTileDraw(batch, delta, x, y, 4);
                }
                if (drawExit && tile.getTileExit() != null && !(new WorldPosition().equals(tile.getTileExit()))) {
                    // draw exit
                    doTileDraw(batch, delta, x, y, 3);
                }
            }
        }
    }

    public void doTileDrawFlipped(SpriteBatch batch, float delta, int x, int y, int graphic) {
        // TODO Refactor maps layers to have animations separated
        AOTexture texture = animationHandler.getTexture(graphic);
        TextureRegion tileRegion = getTextureRegion(delta, graphic, texture);
        if (tileRegion != null && !tileRegion.isFlipY()) {
            tileRegion.flip(false, true);
        }
        doTileDraw(batch, y, x, tileRegion);
    }

    public TextureRegion getTextureRegion(float delta, int graphic, AOTexture texture) {
        TextureRegion tileRegion = null;
        if (texture != null) {
            // TODO CACHE
            tileRegion = texture.getTexture();
        } else {
            BundledAnimation animation = animationHandler.getAnimation(graphic);
            if (animation != null) {
                animation.setAnimationTime(animation.getAnimationTime() + delta);
                tileRegion = animation.getGraphic();
            }
        }
        return tileRegion;
    }

    public void doTileDraw(SpriteBatch batch, float delta, int x, int y, int graphic) {
        // TODO Refactor maps layers to have animations separated
        TextureRegion tileRegion = null;
        AOTexture texture = animationHandler.getTexture(graphic);
        tileRegion = getTextureRegion(delta, graphic, texture);

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
    protected void processSystem() {
    }
}
