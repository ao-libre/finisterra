package game.handlers;

import shared.model.map.Map;
import shared.util.MapHelper;

import java.util.HashMap;

public class MapHandler {

    private static HashMap<Integer, Map> maps = new HashMap();

    private static MapHelper helper = MapHelper.instance();

    public static void load() {
        helper.initializeMaps(maps);
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
        Map map = helper.getMap(mapNumber);
        maps.put(mapNumber, map);
        return map;
    }

    public static MapHelper getHelper() {
        return helper;
    }
}
