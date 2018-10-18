package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.utils.WorldUtils;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import position.WorldPos;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

public class MapManager {

    private static Map<Integer, Set<Integer>> nearEntities = new ConcurrentHashMap<>();
    private static Map<Integer, Set<Integer>> entitiesByMap = new ConcurrentHashMap<>();

    private static HashMap<Integer, ar.com.tamborindeguy.model.map.Map> maps = new HashMap<>();

    public static Set<Integer> getNearEntities(int entityId) {
        return nearEntities.getOrDefault(entityId, Collections.emptySet());
    }

    public static Set<Integer> getEntitiesInMap(int map) {
        return entitiesByMap.get(map);
    }

    public static void movePlayer(int player, Optional<WorldPos> previusPos) {
        WorldPos actualPos = E(player).getWorldPos();
        previusPos.ifPresent(it -> {
            if (it.equals(actualPos)) {
                return;
            }
            if (it.map != actualPos.map) {
                getEntitiesInMap(it.map).remove(player);
            }
            Set<Integer> near = new HashSet<>(nearEntities.get(player));
            near.forEach(nearEntity -> {
                removeNearEntity(player, nearEntity);
            });
        });
        addPlayer(player);
    }

    public static void removeEntity(int entity) {
        int map = E(entity).getWorldPos().map;
        // remove from near entities
        nearEntities.computeIfPresent(entity, (player, removeFrom) -> {
            removeFrom.forEach(nearEntity -> {
                unlinkEntities(nearEntity, entity);
            });
            return null;
        });
        entitiesByMap.get(map).remove(entity);
    }

    public static void addPlayer(int player) {
        int map = E(player).getWorldPos().map;
        Set<Integer> entities = entitiesByMap.computeIfAbsent(map, (it) -> new HashSet<>());
        entities.add(player);
        entities.stream()
                .filter(entity -> entity != player)
                .forEach(entity -> {
                    addNearEntities(player, entity);
                });
    }

    public static void addItem(int item) {
        int map = E(item).getWorldPos().map;
        Set<Integer> entities = entitiesByMap.computeIfAbsent(map, (it) -> new HashSet<>());
        entities.add(item);
        entities.stream()
                .filter(entity -> entity != item)
                .forEach(entity -> {
                    addNearEntities(item, entity);
                });
    }

    private static void addNearEntities(int entity1, int entity2) {
        int distance = WorldUtils.distance(E(entity2).getWorldPos(), E(entity1).getWorldPos());
        if (distance >= 0 && distance <= 15) {
            linkEntities(entity1, entity2);
            linkEntities(entity2, entity1);
        }
    }

    private static void removeNearEntity(int player1, int player2) {
        int distance = WorldUtils.distance(E(player2).getWorldPos(), E(player1).getWorldPos());
        if (distance < 0 || distance > 15) {
            unlinkEntities(player1, player2);
            unlinkEntities(player2, player1);
        }
    }

    private static void unlinkEntities(int entity1, int entity2) {
        if (nearEntities.containsKey(entity1)) {
            nearEntities.get(entity1).remove(entity2);
        }
        // always notify that this entity is not longer in range
        WorldManager.sendEntityRemove(entity1, entity2);
    }

    private static void linkEntities(int entity1, int entity2) {
        Set<Integer> near = nearEntities.computeIfAbsent(entity1, (i) -> new HashSet<>());
        if (!near.contains(entity2)) {
            near.add(entity2);
            EntityUpdate update = new EntityUpdate(entity2, WorldUtils.getComponents(entity2), new Class[0]);
            WorldManager.sendEntityUpdate(entity1, update);
        }
    }

    public static void initialize() {
        Log.info("Loading maps...");
        for (int i = 1; i <= 290; i++) {
            //                FileInputStream mapStream = new FileInputStream("resources/maps/" + "Mapa" + i + ".json");
            InputStream mapStream = MapManager.class.getClassLoader().getResourceAsStream("maps/" + "Mapa" + i + ".json");
            ar.com.tamborindeguy.model.map.Map map = getJson().fromJson(ar.com.tamborindeguy.model.map.Map.class, mapStream);
            maps.put(i, map);
        }
    }

    private static Json getJson() {
        Json json = new Json();
        json.addClassTag("map", ar.com.tamborindeguy.model.map.Map.class);
        return json;
    }

    public static Optional<Tile> getTile(WorldPos expectedPos) {
        ar.com.tamborindeguy.model.map.Map map = maps.get(expectedPos.map);
        return Optional.ofNullable(map.getTile(expectedPos.x, expectedPos.y));
    }

    public static boolean isValidPos(WorldPos worldPos) {
        ar.com.tamborindeguy.model.map.Map map = maps.get(worldPos.map);
        Set<Integer> playersInMap = MapManager.getEntitiesInMap(worldPos.map);
        if (playersInMap.stream().anyMatch(player -> (E(player).getWorldPos().x == worldPos.x) && (E(player).getWorldPos().y == worldPos.y))) {
            return false;
        }
//        return MapUtils.isValidPos(map, worldPos);
        // TODO
        return true;
    }

    public static ar.com.tamborindeguy.model.map.Map get(int mapNumber) {
        return maps.get(mapNumber);
    }
}
