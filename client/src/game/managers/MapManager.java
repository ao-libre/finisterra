package game.managers;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.g2d.TextureArraySpriteBatch;
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

    public void drawLayer(Map map, TextureArraySpriteBatch batch, int layer) {
        drawLayer(map, batch, 0, layer, false, false, false);
    }

    private void drawLayer(Map map, TextureArraySpriteBatch batch, float delta, int layer, boolean drawExit, boolean drawBlock, boolean flip) {
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
                if (flip) {
                    doTileDrawFlipped(batch, delta, x, y, graphic);
                } else {
                    doTileDraw(batch, delta, x, y, graphic);
                }
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

    public void drawLayer(Map map, TextureArraySpriteBatch batch, float delta, int layer, boolean drawExit, boolean drawBlock) {
        drawLayer(map, batch, delta, layer, drawExit, drawBlock, true);
    }

    private void doTileDrawFlipped(TextureArraySpriteBatch batch, float delta, int x, int y, int graphic) {
        TextureRegion tileRegion = animationHandler.hasTexture(graphic) ?
                getTextureRegion(animationHandler.getTexture(graphic)) :
                getAnimation(delta, graphic);
        if (tileRegion != null && !tileRegion.isFlipY()) {
            tileRegion.flip(false, true);
        }
        doTileDraw(batch, y, x, tileRegion);
    }

    private TextureRegion getAnimation(float delta, int graphic) {
        TextureRegion tileRegion = null;
        BundledAnimation animation = animationHandler.getTiledAnimation(graphic);
        if (animation != null) {
            tileRegion = animation.getGraphic();
        }
        return tileRegion;
    }

    private TextureRegion getTextureRegion(AOTexture texture) {
        TextureRegion tileRegion = null;
        if (texture != null) {
            tileRegion = texture.getTexture();
        }
        return tileRegion;
    }

    public void doTileDraw(TextureArraySpriteBatch batch, float delta, int x, int y, int graphic) {
        TextureRegion tileRegion = animationHandler.hasTexture(graphic) ?
                getTextureRegion(animationHandler.getTexture(graphic)) :
                getAnimation(delta, graphic);
        doTileDraw(batch, y, x, tileRegion);
    }

    private void doTileDraw(TextureArraySpriteBatch batch, int y, int x, TextureRegion tileRegion) {
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
