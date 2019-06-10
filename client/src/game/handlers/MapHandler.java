package game.handlers;

import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.util.MapHelper;

public class MapHandler {

    private static MapHelper helper;

    public static MapHelper getHelper() {
        if (helper == null) {
            helper = MapHelper.instance();
        }
        return helper;
    }

    public static Map get(int map) {
        return helper.getMap(map);
    }

    public static Tile getTile(WorldPos pos) {
        Map map = get(pos.map);
        return helper.getTile(map, pos);
    }
}
