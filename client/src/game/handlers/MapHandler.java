package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import game.managers.MapManager;
import shared.model.map.Map;
import shared.util.MapUtils;

import java.util.HashMap;

public class MapHandler {

    private static HashMap<Integer, Map> maps = new HashMap();

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
        Map map = MapUtils.getMap(mapNumber);
        Log.debug("Map " + mapNumber + ".json successfully loaded in " + (System.currentTimeMillis() - start) + "ms");
        MapManager.initialize(map);
        maps.put(mapNumber, map);
        return map;
    }

}
