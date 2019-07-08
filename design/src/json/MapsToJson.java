package json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import shared.model.map.Map;
import shared.util.AOJson;
import shared.util.MapHelper;

import java.util.concurrent.ConcurrentMap;

public class MapsToJson {

    public static void transformToJson() {
        MapHelper helper = MapHelper.instance(MapHelper.CacheStrategy.NEVER_EXPIRE);
        helper.getMaps().forEach((id, map) -> {
            map.setNeighbours(
                    helper.getMap(MapHelper.Dir.LEFT, map),
                    helper.getMap(MapHelper.Dir.UP, map),
                    helper.getMap(MapHelper.Dir.RIGHT, map),
                    helper.getMap(MapHelper.Dir.DOWN, map)
            );
        });
        saveMaps(helper.getMaps());
    }

    private static void saveMaps(ConcurrentMap<Integer, Map> maps) {
        Json json = new AOJson();
        FileHandle folder = Gdx.files.local("output/maps/");
        maps.forEach((id, map) -> {
            FileHandle mapFile = folder.child("Map" + id + ".json");
            json.toJson(map, mapFile);
        });
    }

}
