package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.client.game.MapManager;
import ar.com.tamborindeguy.model.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;

import java.util.HashMap;

public class MapHandler {

    private static HashMap<Integer, Map> maps = new HashMap();

    public static Map get(int map) {
        if (!maps.containsKey(map)) {
            return load(map);
        }
        return maps.get(map);
    }

    public static boolean has(int map) {
        return maps.containsKey(map);
    }

    public static Map load(int mapNumber) {
        long start = System.currentTimeMillis();
        Map map = getJson().fromJson(Map.class, Gdx.files.internal("data/maps/" + "Mapa" + mapNumber + ".json"));
        Log.debug("Map " + mapNumber + ".map successfully loaded in " + (System.currentTimeMillis() - start) + "ms");
        MapManager.initialize(map);
        maps.put(mapNumber, map);
        return map;
    }

    private static Json getJson() {
        Json json = new Json();
        json.addClassTag("map", Map.class);
        return json;
    }

}
