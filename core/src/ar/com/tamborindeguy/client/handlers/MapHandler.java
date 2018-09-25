package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.client.game.MapManager;
import ar.com.tamborindeguy.model.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;

public class MapHandler {

    private static HashMap<Integer, Map> maps = new HashMap();

    public static Map get(int mapNumber) {
        if (!maps.containsKey(mapNumber)) load(mapNumber);
        return maps.get(mapNumber);
    }

    public static boolean has(int mapNumber) {
        return maps.containsKey(mapNumber);
    }

    public static void add(int mapNumber, Map map) {
        MapManager.initialize(map);
        maps.put(mapNumber, map);
    }

    private static Map load(int mapNumber) {

        Map map = getJson().fromJson(Map.class, Gdx.files.internal("data/maps/" + "Mapa" + mapNumber + ".json"));
        maps.put(mapNumber, map);

        Gdx.app.log(MapHandler.class.getSimpleName(),
                "[MapHandler] Map " + String.valueOf(mapNumber)
                        + ".map successfully loaded");
        return map;
    }

    private static Json getJson() {
        Json json = new Json();
        json.addClassTag("map", Map.class);
        return json;
    }

    public static void load() {
        for (int i = 1; i <= 290; i++) {
            add(i, load(i));
        }
    }
}
