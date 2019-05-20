package shared.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.util.HashMap;
import java.util.Set;

import static com.artemis.E.E;
import static java.lang.String.format;

public class MapHelper {

    public static final int MAP_COUNT = 1; // TODO set to 1 to load faster
    private static Json mapJson = new Json();

    private MapHelper() {}

    public static MapHelper instance() {
        return new MapHelper();
    }

    public boolean isBlocked(Map map, WorldPos pos) {
        return isBlocked(map, pos.x, pos.y);
    }

    public boolean isBlocked(Map map, int x, int y) {
        Tile tile = map.getTile(x, y);
        return tile.isBlocked();
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
     * Initialize maps. TODO refactor
     */
    public void initializeMaps(HashMap<Integer, Map> maps) {
        Log.info("Loading maps...");
        for (int i = 1; i <= MAP_COUNT; i++) {
            Map map = getMap(i);
            maps.put(i, map);
        }
    }

    public Map getMap(int i) {
        FileHandle mapFile = Gdx.files.internal(SharedResources.MAPS_FOLDER + "Mapa" + i + ".json");
        return mapJson.fromJson(Map.class, mapFile);
    }


}
