package game.systems.resources;

import net.mostlyoriginal.api.system.core.PassiveSystem;
import component.position.WorldPos;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.util.MapHelper;

import static shared.util.MapHelper.CacheStrategy.FIVE_MIN_EXPIRE;

public class MapSystem extends PassiveSystem {

    private static MapHelper helper;

    public static MapHelper getHelper() {
        if (helper == null) {
            helper = MapHelper.instance(FIVE_MIN_EXPIRE);
        }
        return helper;
    }

    public static Map get(int map) {
        return getHelper().getMap(map);
    }

    public static Tile getTile(WorldPos pos) {
        return MapHelper.getTile(get(pos.map), pos);
    }
}
