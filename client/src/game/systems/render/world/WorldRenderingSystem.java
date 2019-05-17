package game.systems.render.world;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.EBag;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.handlers.AnimationHandler;
import game.handlers.MapHandler;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import model.textures.BundledAnimation;
import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.util.Optional;

@Wire
public class WorldRenderingSystem extends BaseSystem {

    private SpriteBatch batch;
    private CameraSystem cameraSystem;
    private TiledMapSystem tiledMapSystem;
    private CharacterRenderingSystem characterRenderingSystem;

    public WorldRenderingSystem(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    protected void begin() {
        getCamera().update();
        getBatch().setProjectionMatrix(getCamera().combined);
        getBatch().begin();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    private Camera getCamera() {
        return cameraSystem.camera;
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void processSystem() {
        int mapNumber = tiledMapSystem.mapNumber;
        if (mapNumber > 0) {
            getRange().forEachTile((x, y) -> {
                WorldPos pos = new WorldPos(x, y, mapNumber);
                getMapElement(pos).ifPresent(element -> drawTile(batch, world.getDelta(), element, x, y));
                getPlayer(pos).ifPresent(characterRenderingSystem::drawPlayer);
            });
        }
    }


    public UserRange getRange() {
        UserRange range = new UserRange();

        // Calculate visible part of the map
        int cameraPosX = (int) (this.cameraSystem.camera.position.x / Tile.TILE_PIXEL_WIDTH);
        int cameraPosY = (int) (this.cameraSystem.camera.position.y / Tile.TILE_PIXEL_HEIGHT);
        int halfWindowTileWidth = (int) ((this.cameraSystem.camera.viewportWidth / Tile.TILE_PIXEL_WIDTH) / 2f);
        int halfWindowTileHeight = (int) ((this.cameraSystem.camera.viewportHeight / Tile.TILE_PIXEL_HEIGHT) / 2f);

        int screenMinX = cameraPosX - halfWindowTileWidth - 1;
        int screenMaxX = cameraPosX + halfWindowTileWidth + 1;
        int screenMinY = cameraPosY - halfWindowTileHeight - 1;
        int screenMaxY = cameraPosY + halfWindowTileHeight + 1;

        range.minAreaX = MathUtils.clamp(screenMinX - Map.TILE_BUFFER_SIZE, Map.MIN_MAP_SIZE_WIDTH, Map.MAX_MAP_SIZE_WIDTH);
        range.maxAreaX = MathUtils.clamp(screenMaxX + Map.TILE_BUFFER_SIZE, Map.MIN_MAP_SIZE_WIDTH, Map.MAX_MAP_SIZE_WIDTH);
        range.minAreaY = MathUtils.clamp(screenMinY - Map.TILE_BUFFER_SIZE,  Map.MIN_MAP_SIZE_HEIGHT, Map.MAX_MAP_SIZE_HEIGHT);
        range.maxAreaY = MathUtils.clamp(screenMaxY + Map.TILE_BUFFER_SIZE,  Map.MIN_MAP_SIZE_HEIGHT, Map.MAX_MAP_SIZE_HEIGHT);

        return range;
    }

    private void drawTile(SpriteBatch batch, float delta, int graphic, int x, int y) {
        BundledAnimation animation = AnimationHandler.getGraphicAnimation(graphic);
        TextureRegion tileRegion = animation.isAnimated() ? animation.getAnimatedGraphic(true) : animation.getGraphic();

        if (animation.isAnimated()) {
            animation.setAnimationTime(animation.getAnimationTime() + delta);
        }

        if (tileRegion != null) {
            final float mapPosX = (x * Tile.TILE_PIXEL_WIDTH);
            final float mapPosY = (y * Tile.TILE_PIXEL_HEIGHT);
            final float tileOffsetX = mapPosX + (Tile.TILE_PIXEL_WIDTH - tileRegion.getRegionWidth()) / 2;
            final float tileOffsetY = mapPosY - tileRegion.getRegionHeight() + Tile.TILE_PIXEL_HEIGHT;
            batch.draw(tileRegion, tileOffsetX, tileOffsetY);
        }
    }

    public static class UserRange {
        int minAreaX, minAreaY, maxAreaX, maxAreaY;

        void forEachTile(TileDraw tile) {
            for (int y = minAreaY; y <= maxAreaY; y++) {
                for (int x = minAreaX; x <= maxAreaX; x++) {
                    tile.doDraw(x,y);
                }
            }
        }
    }

    public interface TileDraw {
        void doDraw(int x, int y);
    }

    private Optional<Integer> getMapElement(WorldPos pos) {
        Optional<Integer> result = Optional.empty();
        Tile tile = MapHandler.get(pos.map).getTile(pos.x, pos.y);
        int element = tile.getGraphic(2);
        if (element != 0) {
            result = Optional.of(element);
        }
        return result;
    }

    private Optional<E> getPlayer(WorldPos pos) {
        EBag characters = E.withAspect(characterRenderingSystem.getAspect());
        Optional<E> result = Optional.empty();
        for (E character : characters) {
            if (character.getWorldPos().equals(pos)) {
                result = Optional.of(character);
            }
        }
        return result;
    }
}
