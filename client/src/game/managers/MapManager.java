package game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import game.handlers.AnimationHandler;
import model.textures.BundledAnimation;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MapManager {

    public static final List<Integer> LOWER_LAYERS = Arrays.asList(0, 1);
    public static final List<Integer> UPPER_LAYERS = Collections.singletonList(3);

    public static final int TILE_BUFFER_SIZE = 7;

    public static final int MAX_MAP_SIZE_WIDTH = 100;
    public static final int MIN_MAP_SIZE_WIDTH = 1;
    public static final int MAX_MAP_SIZE_HEIGHT = 100;
    public static final int MIN_MAP_SIZE_HEIGHT = 1;

    private static java.util.Map<Map, Texture> bufferedLayers = new HashMap<>();

    public static void renderLayerToBuffer(Map map) {
        renderLayerToBuffer(map, 0);
    }

    public static void renderLayerToBuffer(Map map, int layer) {

        int width = (int) (MAX_MAP_SIZE_WIDTH * Tile.TILE_PIXEL_WIDTH);
        int height = (int) (MAX_MAP_SIZE_HEIGHT * Tile.TILE_PIXEL_HEIGHT);

        OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.setToOrtho(true, width, height);

        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(camera.combined);
        fbo.begin();

        sb.enableBlending();
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.begin();
        renderLayer(map, sb, layer);
        sb.end();

        fbo.end();
        bufferedLayers.put(map, fbo.getColorBufferTexture());
    }

    public static void renderLayer(Map map, SpriteBatch batch) {
        renderLayer(map, batch, 0, 0, MIN_MAP_SIZE_WIDTH, MAX_MAP_SIZE_WIDTH, MIN_MAP_SIZE_HEIGHT, MAX_MAP_SIZE_HEIGHT);
    }

    public static void renderLayer(Map map, SpriteBatch batch, int layer) {
        renderLayer(map, batch, 0.0f, layer, MIN_MAP_SIZE_WIDTH, MAX_MAP_SIZE_WIDTH, MIN_MAP_SIZE_HEIGHT, MAX_MAP_SIZE_HEIGHT);
    }

    public static void renderLayer(Map map, SpriteBatch batch, float delta, int layer) {
        renderLayer(map, batch, delta, layer, MIN_MAP_SIZE_WIDTH, MAX_MAP_SIZE_WIDTH, MIN_MAP_SIZE_HEIGHT, MAX_MAP_SIZE_HEIGHT);
    }

    public static void renderLayer(Map map, SpriteBatch batch, float delta, int layer, int minX, int maxX, int minY, int maxY) {
        Color color = batch.getColor();
        if (layer >= 2) {
            batch.setColor(color.r, color.g, color.b, 0.7f);
        }
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                if (map.getTile(x, y) != null) {
                    drawTile(map, batch, delta, layer, y, x);
                }
            }
        }
    }

    public static void drawTile(Map map, SpriteBatch batch, float delta, int layer, int y, int x) {
        int graphic = map.getTile(x, y).getGraphic(layer);
        if (graphic == 0) {
            return;
        }

        doTileDraw(batch, delta, y, x, graphic);
    }

    public static void doTileDraw(SpriteBatch batch, float delta, int y, int x, int graphic) {
        BundledAnimation animation = AnimationHandler.getGraphicAnimation(graphic);
        TextureRegion tileRegion = animation.isAnimated() ? animation.getAnimatedGraphic(true) : animation.getGraphic();

        if (animation.isAnimated()) {
            animation.setAnimationTime(animation.getAnimationTime() + delta);
        }

        doTileDraw(batch, y, x, tileRegion);
    }

    public static void doTileDraw(SpriteBatch batch, int y, int x, TextureRegion tileRegion) {
        if (tileRegion != null) {
            final float mapPosX = (x * Tile.TILE_PIXEL_WIDTH);
            final float mapPosY = (y * Tile.TILE_PIXEL_HEIGHT);
            final float tileOffsetX = mapPosX + (Tile.TILE_PIXEL_WIDTH - tileRegion.getRegionWidth()) / 2;
            final float tileOffsetY = mapPosY - tileRegion.getRegionHeight() + Tile.TILE_PIXEL_HEIGHT;
            batch.draw(tileRegion, tileOffsetX, tileOffsetY);
        }
    }

    public static boolean isLoaded(Map map) {
        return bufferedLayers.containsKey(map);
    }

    public static Texture getBufferedLayer(Map map) {
        return bufferedLayers.get(map);
    }

    public static void initialize(Map map) {
        renderLayerToBuffer(map);
    }

}
