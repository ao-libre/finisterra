package ar.com.tamborindeguy.util;

import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.model.map.Tile;
import position.WorldPos;

import java.util.Set;

import static com.artemis.E.E;

public class MapUtils {

    public static boolean isBlocked(Map map, WorldPos pos) {
        Tile tile = map.getTile(pos.x, pos.y);
        return tile.isBlocked();
    }

    public static boolean hasEntity(Set<Integer> entities, WorldPos pos) {
        return entities.stream().anyMatch(entity -> {
            boolean isObject = E(entity).hasObject();
            boolean samePos = E(entity).hasWorldPos() && pos.equals(E(entity).getWorldPos());
            boolean hasSameDestination = E(entity).hasMovement() && E(entity).getMovement().destinations.stream().anyMatch(destination -> destination.equals(pos));
            return !isObject && (samePos || hasSameDestination);
        });
    }

}
