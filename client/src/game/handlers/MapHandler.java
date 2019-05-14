package game.handlers;

import com.esotericsoftware.minlog.Log;
import game.managers.MapManager;
import shared.model.map.Map;
import shared.util.MapHelper;

import java.util.HashMap;

public class MapHandler {

    private static HashMap<Integer, Map> maps = new HashMap();

    private static MapHelper helper = MapHelper.instance();

    static {
        load(1);
    }

    public static Map get(int map) {
        if (!has(map)) {
            return load(map);
        }
        return maps.get(map);
    }

    public static boolean has(int map) {
        return maps.containsKey(map);
    }

    public static Map load(int mapNumber) {
        long start = System.currentTimeMillis();
        Map map = helper.getMap(mapNumber);
        Log.debug("Map " + mapNumber + ".json successfully loaded in " + (System.currentTimeMillis() - start) + "ms");
        MapManager.initialize(map);
        maps.put(mapNumber, map);
        return map;
    }

    public static MapHelper getHelper() {
        return helper;
    }
}
