package shared.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import position.WorldPos;
import shared.model.loaders.MapLoader;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static com.artemis.E.E;

public class MapHelper {

    public static final int MAP_COUNT = 1; // TODO set to 1 to load faster
    private static Json mapJson = new Json();

    private MapHelper() {
    }

    public static MapHelper instance() {
        return new MapHelper();
    }

    public boolean isBlocked(Map map, WorldPos pos) {
        return isBlocked(map, pos.x, pos.y);
    }

    public boolean isBlocked(Map map, int x, int y) {
        Tile tile = map.getTile(x, y);
        return tile != null && tile.isBlocked();
    }

    public boolean hasEntity(Set<Integer> entities, WorldPos pos) {
        return entities.stream().anyMatch(entity -> {
            boolean isObject = E(entity).hasObject();
            boolean isFootPrint = E(entity).hasFootprint();
            boolean samePos = E(entity).hasWorldPos() && pos.equals(E(entity).getWorldPos());
            boolean hasSameDestination = E(entity).hasMovement() && E(entity).getMovement().destinations.stream().anyMatch(destination -> destination.worldPos.equals(pos));
            return !isObject && !isFootPrint && (samePos || hasSameDestination);
        });
    }

    /**
     * Initialize maps.
     */
    public void initializeMaps(HashMap<Integer, Map> maps) {
        Log.info("Loading maps...");
        for (int i = 1; i <= 1; i++) {
            Map map = getFromJson(i);
            maps.put(i, map);
        }
    }

    public void getAlkonMaps(HashMap<Integer, Map> maps) {
        Log.info("Loading maps...");
        for (int i = 1; i <= 290; i++) {
            Map map = getMap(i);
            maps.put(i, map);
        }
    }

    public Map getMap(int i) {
        FileHandle mapPath = Gdx.files.internal(SharedResources.MAPS_FOLDER + "Alkon/Mapa" + i + ".map");
        FileHandle infPath = Gdx.files.internal(SharedResources.MAPS_FOLDER + "Alkon/Mapa" + i + ".inf");
        MapLoader loader = new MapLoader();
        try (DataInputStream map = new DataInputStream(mapPath.read());
             DataInputStream inf = new DataInputStream(infPath.read())) {
            return loader.load(map, inf);
        } catch (IOException | GdxRuntimeException e) {
            e.printStackTrace();
            Log.info("Failed to read map " + i);
            return new Map();
        }
    }

    public Map getFromJson(int i) {
        Json json = new Json();
        Map map = json.fromJson(Map.class, Gdx.files.internal(SharedResources.MAPS_FOLDER + "world/" + i + ".json"));
        return map;
    }

    public boolean hasTileExit(Map map, WorldPos expectedPos) {
        Tile tile = map.getTile(expectedPos.x, expectedPos.y);
        return tile != null && tile.getTileExit() != null;
    }

    public Tile getTile(WorldPos pos) {
        Map map = getMap(pos.map);
        if (pos.x > 0 && pos.x < map.getWidth()) {
            if (pos.y > 0 && pos.y < map.getHeight()) {
                return map.getTile(pos.x, pos.y);
            }
        }
        return null;
    }
}
