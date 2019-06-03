package game.handlers;

import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;
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

    public static Tile getTile(WorldPos pos) {
        Map map = get(pos.map);
        return helper.getTile(map, pos);
    }
}
