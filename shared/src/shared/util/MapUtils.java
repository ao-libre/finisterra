package shared.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;

import static com.artemis.E.E;

public class MapUtils {

    public static final int MAP_COUNT = 1; // TODO set to 1 to load faster
    private static Json mapJson = new Json();
    static {
        mapJson.addClassTag("map", shared.model.map.Map.class);
    }

    public static boolean isBlocked(Map map, WorldPos pos) {
        Tile tile = map.getTile(pos.x, pos.y);
        return tile.isBlocked();
    }

    public static boolean hasEntity(Set<Integer> entities, WorldPos pos) {
        return entities.stream().anyMatch(entity -> {
            boolean isObject = E(entity).hasObject();
            boolean samePos = E(entity).hasWorldPos() && pos.equals(E(entity).getWorldPos());
            boolean hasSameDestination = E(entity).hasMovement() && E(entity).getMovement().destinations.stream().anyMatch(destination -> destination.equals(pos));
            return !isObject && (samePos || hasSameDestination);
        });
    }

    /**
     * Initialize maps. TODO refactor
     */
    public static void initializeMaps(HashMap<Integer, Map> maps) {
        Log.info("Loading maps...");
        for (int i = 1; i <= MAP_COUNT; i++) {
            maps.put(i, getMap(i));
        }
    }

    public static Map getMap(int i) {
        FileHandle mapFile = Gdx.files.internal(SharedResources.MAPS_FOLDER + "Mapa" + i + ".json");
        return mapJson.fromJson(Map.class, mapFile);
    }


}
