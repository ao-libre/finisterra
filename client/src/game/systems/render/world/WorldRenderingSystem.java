package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.EBag;
import com.artemis.annotations.Wire;
import component.graphic.Effect;
import component.graphic.RenderBefore;
import component.position.WorldPos;
import game.systems.camera.CameraSystem;
import game.systems.map.MapManager;
import game.systems.map.TiledMapSystem;
import game.systems.resources.MapSystem;
import game.systems.world.NetworkedEntitySystem;
import shared.model.map.Tile;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Wire
public class WorldRenderingSystem extends BaseSystem {

    private static final int EXTRA_TILES = 7;
    private MapManager mapManager;
    private CameraSystem cameraSystem;
    private TiledMapSystem tiledMapSystem;
    private CharacterRenderingSystem characterRenderingSystem;
    private EffectRenderingSystem effectRenderingSystem;
    private NetworkedEntitySystem networkedEntitySystem;

    public WorldRenderingSystem() {
    }

    @Override
    protected void processSystem() {
        int mapNumber = tiledMapSystem.mapNumber;
        if (mapNumber > 0) {
            getRange().forEachTile((x, y) -> {
                WorldPos pos = MapSystem.getHelper().getEffectivePosition(mapNumber, x, y);
                getMapElement(pos).ifPresent(element -> mapManager.doTileDraw(world.getDelta(), x, y, element));
                getBeforeEffect(pos).forEach(e -> effectRenderingSystem.drawEffect(e, e.hasWorldPos() ? translatePos(e.getWorldPos(), x, y) : Optional.empty()));
                getPlayer(pos).ifPresent(e -> characterRenderingSystem.drawPlayer(e, translatePos(e.getWorldPos(), x, y)));
                getAfterEffect(pos).forEach(e -> effectRenderingSystem.drawEffect(e, e.hasWorldPos() ? translatePos(e.getWorldPos(), x, y) : Optional.empty()));
            });
        }
    }

    private Optional<WorldPos> translatePos(WorldPos pos, int x, int y) {
        Optional<WorldPos> result = Optional.empty();
        if (pos.x != x || pos.y != y) {
            WorldPos newPos = new WorldPos(x, y, pos.map);
            result = Optional.of(newPos);
        }
        return result;
    }

    private Set<E> getBeforeEffect(WorldPos pos) {
        EBag effects = E.withAspect(Aspect.all(Effect.class, RenderBefore.class));
        return getEffect(effects, pos);
    }

    private Set<E> getEffect(EBag effects, WorldPos pos) {
        Set<E> result = new HashSet<>();
        effects.forEach(result::add);
        return result
                .stream()
                .filter(e -> {
                    if (e.hasWorldPos()) {
                        return e.getWorldPos().equals(pos);
                    } else if (e.hasRef()) {
                        int entityReference = e.refId();
                        E entity = E.E(entityReference);
                        if (entity != null && entity.hasWorldPos()) {
                            return entity.getWorldPos().equals(pos);
                        }
                    }
                    return false;
                })
                .collect(Collectors.toSet());
    }

    private Set<E> getAfterEffect(WorldPos player) {
        EBag effects = E.withAspect(Aspect.all(Effect.class).exclude(RenderBefore.class));
        return getEffect(effects, player);
    }

    UserRange getRange() {
        UserRange range = new UserRange();
        // Calculate visible part of the map
        int cameraPosX = (int) (this.cameraSystem.camera.position.x / Tile.TILE_PIXEL_WIDTH);
        int cameraPosY = (int) (this.cameraSystem.camera.position.y / Tile.TILE_PIXEL_HEIGHT);
        int halfWindowTileWidth = (int) ((this.cameraSystem.camera.viewportWidth / Tile.TILE_PIXEL_WIDTH) / 2f);
        int halfWindowTileHeight = (int) ((this.cameraSystem.camera.viewportHeight / Tile.TILE_PIXEL_HEIGHT) / 2f);

        range.minAreaX = cameraPosX - halfWindowTileWidth - EXTRA_TILES;
        range.maxAreaX = cameraPosX + halfWindowTileWidth + EXTRA_TILES;
        range.minAreaY = cameraPosY - halfWindowTileHeight - EXTRA_TILES;
        range.maxAreaY = cameraPosY + halfWindowTileHeight + EXTRA_TILES;

        return range;
    }

    private Optional<Integer> getMapElement(WorldPos pos) {
        Optional<Integer> result = Optional.empty();

        Tile tile = MapSystem.getTile(pos);
        if (tile != null) {
            int element = tile.getGraphic(2);
            if (element != 0) {
                result = Optional.of(element);
            }
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

    public interface TileDraw {
        void doDraw(int x, int y);
    }

    static class UserRange {
        int minAreaX, minAreaY, maxAreaX, maxAreaY;

        void forEachTile(TileDraw tile) {
            for (int y = minAreaY; y <= maxAreaY; y++) {
                for (int x = minAreaX; x <= maxAreaX; x++) {
                    tile.doDraw(x, y);
                }
            }
        }
    }
}
