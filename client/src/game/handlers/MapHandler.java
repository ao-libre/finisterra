package game.handlers;

import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.util.MapHelper;

import java.util.HashMap;

public class MapHandler {

    private static MapHelper helper;

    public static MapHelper getHelper() {
        if (helper == null) {
            helper = MapHelper.instance();
        }
        return helper;
    }

    public static boolean has(int map) {
        return helper.hasMap(map);
    }

    public static Map get(int map) {
        if (!has(map)) {
            return load(map);
        }
        return helper.getMap(map);
    }

    public static Map load(int mapNumber) {
        return helper.getMap(mapNumber);
    }

    public static Tile getTile(WorldPos pos) {
        Map map = get(pos.map);
        return helper.getTile(map, pos);
    }
}
