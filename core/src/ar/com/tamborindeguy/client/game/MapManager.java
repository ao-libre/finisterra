package ar.com.tamborindeguy.client.game;

import ar.com.tamborindeguy.client.handlers.AnimationsHandler;
import ar.com.tamborindeguy.model.textures.BundledAnimation;
import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.model.map.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.HashMap;

public class MapManager {

    public static final int TILE_BUFFER_SIZE = 7;

    public static final int MAX_MAP_SIZE_WIDTH = 100;
    public static final int MIN_MAP_SIZE_WIDTH = 1;
    public static final int MAX_MAP_SIZE_HEIGHT = 100;
    public static final int MIN_MAP_SIZE_HEIGHT = 1;

    private static java.util.Map<Map, Texture> bufferedLayers = new HashMap<>();

    /**
     * Renders map's first layer to it's internal FrameBuffer Object
     *
     * @return void
     */
    public static void renderLayerToBuffer(Map map) {
        renderLayerToBuffer(map, 0);
    }

    /**
     * Renders a map layer to it's internal FrameBuffer Object
     *
     * @param layer
     * @return void
     */
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

    /**
     * Renders a Map Layer to a specific SpriteBatch
     *
     * @param batch
     * @return void
     */
    public static void renderLayer(Map map, SpriteBatch batch) {
        renderLayer(map, batch, 0, 0, MIN_MAP_SIZE_WIDTH, MAX_MAP_SIZE_WIDTH, MIN_MAP_SIZE_HEIGHT, MAX_MAP_SIZE_HEIGHT);
    }

    /**
     * Renders a Map Layer to a specific SpriteBatch
     * @param batch
     * @param layer
     */
    public static void renderLayer(Map map, SpriteBatch batch, int layer) {
        renderLayer(map, batch, 0.0f, layer, MIN_MAP_SIZE_WIDTH, MAX_MAP_SIZE_WIDTH, MIN_MAP_SIZE_HEIGHT, MAX_MAP_SIZE_HEIGHT);
    }

    /**
     * Renders a Map Layer to a specific SpriteBatch
     * @param batch
     * @param layer
     */
    public static void renderLayer(Map map, SpriteBatch batch, float delta, int layer) {
        renderLayer(map, batch, delta, layer, MIN_MAP_SIZE_WIDTH, MAX_MAP_SIZE_WIDTH, MIN_MAP_SIZE_HEIGHT, MAX_MAP_SIZE_HEIGHT);
    }

    /**
     * Renders a Map Layer to a specific SpriteBatch
     * @param batch
     * @param layer
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     */
    public static void renderLayer(Map map, SpriteBatch batch, float delta, int layer, int minX, int maxX, int minY, int maxY) {
        Color color = batch.getColor();
        if (layer >= 2) {
            batch.setColor(color.r, color.g, color.b, 0.7f);
        }
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int graphic = map.getTile(x, y).getGraphic(layer);
                if (graphic == 0) {
                    return;
                }
                BundledAnimation animation = AnimationsHandler.getGraphicAnimation(graphic);
                TextureRegion tileRegion = animation.isAnimated() ? animation.getAnimatedGraphic(true) : animation.getGraphic();

                if (animation != null && animation.isAnimated()) {
                    animation.setAnimationTime(animation.getAnimationTime() + delta);
                }

                if (tileRegion != null) {
                    final float mapPosX = (x * Tile.TILE_PIXEL_WIDTH);
                    final float mapPosY = (y * Tile.TILE_PIXEL_HEIGHT);
                    final float tileOffsetX = mapPosX - (tileRegion.getRegionWidth() * 0.5f) - (16.0f);
                    final float tileOffsetY = mapPosY - tileRegion.getRegionHeight();

                    batch.draw(tileRegion, tileOffsetX, tileOffsetY);
                }
            }
        }
    }

    private static void loadAnimations(Map map) {
        for (int x = 1; x < Map.MAX_MAP_SIZE_WIDTH; x++) {
            for (int y = 1; y < Map.MAX_MAP_SIZE_HEIGHT; y++) {
                for (int grhIndex : map.getTile(x, y).getGraphic()) {
                    if (grhIndex != 0) {
                        AnimationsHandler.saveBundledAnimation(grhIndex);
                    }
                }
            }
        }
    }

    public static boolean isLoaded(Map map) {
        return bufferedLayers.containsKey(map);
    }

    public static Texture getBufferedLayer(Map map) {
        return bufferedLayers.get(map);
    }

    public static void initialize(Map map) {
        loadAnimations(map);
        renderLayerToBuffer(map);
    }
}
