package game.systems.render.world;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import game.systems.resources.AnimationsSystem;
import game.systems.map.MapManager;
import game.systems.render.BatchRenderingSystem;
import org.jetbrains.annotations.NotNull;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Wire(injectInherited = true)
public class MapGroundRenderingSystem extends MapLayerRenderingSystem {

    private static final List<Integer> LOWER_LAYERS = Collections.singletonList(1);
    private final Batch mapBatch;
    // injected systems
    private MapManager mapManager;
    private final LoadingCache<Map, Texture> bufferedLayers = CacheBuilder
            .newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Map, Texture>() {
                @Override
                public Texture load(@NotNull Map key) {
                    return renderLayerToBuffer(key, 0);
                }
            });

    private AnimationsSystem animationsSystem;
    private WorldRenderingSystem worldRenderingSystem;
    private BatchRenderingSystem batchRenderingSystem;

    public MapGroundRenderingSystem() {
        super(LOWER_LAYERS);
        mapBatch = new SpriteBatch();
    }

    @Override
    protected void doRender(Map map) {
        try {
            Texture mapTexture = bufferedLayers.get(map);
            WorldRenderingSystem.UserRange range = worldRenderingSystem.getRange();
            int x = (int) (range.minAreaX * Tile.TILE_PIXEL_WIDTH);
            int y = (int) (range.minAreaY * Tile.TILE_PIXEL_HEIGHT);

            int width = (int) ((range.maxAreaX - range.minAreaX) * Tile.TILE_PIXEL_WIDTH);
            int height = (int) ((range.maxAreaY - range.minAreaY) * Tile.TILE_PIXEL_HEIGHT);

            TextureRegion userRegion = new TextureRegion(mapTexture, x, mapTexture.getHeight() - y - height, width, height);
            batchRenderingSystem.addTask(batch -> batch.draw(userRegion, x, y));
        } catch (ExecutionException e) {
            Log.error("Failed to render map layer 0", e);
        }
        super.doRender(map);
    }

    private Texture renderLayerToBuffer(Map map, int layer) {
        int width = (int) (map.getWidth() * Tile.TILE_PIXEL_WIDTH);
        int height = (int) (map.getHeight() * Tile.TILE_PIXEL_HEIGHT);

        OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.setToOrtho(true, width, height);

        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        mapBatch.setProjectionMatrix(camera.combined);
        fbo.begin();

        mapBatch.enableBlending();
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapBatch.begin();
        renderLayer(map, mapBatch, layer);
        mapBatch.end();

        fbo.end();
        return fbo.getColorBufferTexture();
    }

    private void renderLayer(Map map, Batch mapBatch, int layer) {
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
                doTileDraw(mapBatch, x, y, graphic);

            }
        }
    }

    private void doTileDraw(Batch mapBatch, int x, int y, int graphic) {
        TextureRegion tileRegion = animationsSystem.hasTexture(graphic) ?
                mapManager.getTextureRegion(animationsSystem.getTexture(graphic)) :
                mapManager.getAnimation(0, graphic);
        doTileDraw(mapBatch, y, x, tileRegion);
    }

    private void doTileDraw(Batch mapBatch, int y, int x, TextureRegion tileRegion) {
        if (tileRegion != null) {
            final float mapPosX = (x * Tile.TILE_PIXEL_WIDTH);
            final float mapPosY = (y * Tile.TILE_PIXEL_HEIGHT);
            final float tileOffsetX = mapPosX + (Tile.TILE_PIXEL_WIDTH - tileRegion.getRegionWidth()) / 2;
            final float tileOffsetY = mapPosY - tileRegion.getRegionHeight() + Tile.TILE_PIXEL_HEIGHT;
            mapBatch.draw(tileRegion, tileOffsetX, tileOffsetY);
        }
    }
}
