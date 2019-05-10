package server.systems.manager;

import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.utils.TimeUtils;
import map.Cave;
import position.WorldPos;
import server.core.Server;
import server.map.CaveGenerator;
import shared.network.notifications.EntityUpdate;
import shared.util.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

/**
 * Logic regarding maps, contains information about entities in each map, and how are they related.
 */
public class MapManager extends DefaultManager {

    public static final int MAX_DISTANCE = 15;
    private static HashMap<Integer, shared.model.map.Map> maps = new HashMap<>();
//    public int mapEntity;
    private Map<Integer, Set<Integer>> nearEntities = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesByMap = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesFootprints = new ConcurrentHashMap<>();

    public MapManager(Server server) {
        super(server);
    }

    @Override
    protected void initialize() {
//        initCave();
        MapUtils.initializeMaps(maps);
    }

    private void initCave() {
        CaveGenerator caveGenerator = CaveGenerator.Builder
                .create()
                .height(60)
                .width(60)
                .chanceAlive(0.35f)
                .steps(3)
                .build();
        boolean[][] tiles = caveGenerator.generateMap();

        for (boolean[] row : tiles) {
            String[] s = new String[row.length];
            for (int i = 0; i < row.length; i++) {
                s[i] = row[i] ? "X" : "-";
            }
            System.out.println(Arrays.toString(s));
        }

        Cave cave = new Cave(tiles, 60, 60);
        Entity map = world.createEntity();
        map.edit().add(cave);
//        mapEntity = map.getId();
    }

//    public void generateMapEntity(MapDescriptor descriptor, String path) {
//        int[][] tiles = MapGenerator.generateMap(descriptor);
//        mapEntity = getServer().getWorld().create();
//
//        E map = E(mapEntity).map();
//        map.mapTiles(tiles);
//        map.mapPath(path);
//        map.mapHeight(descriptor.getMapHeight());
//        map.mapWidth(descriptor.getMapWidth());
//    }

    /**
     * @param entityId
     * @return a set of near entities or empty
     */
    public Set<Integer> getNearEntities(int entityId) {
        return nearEntities.getOrDefault(entityId, ConcurrentHashMap.newKeySet());
    }

    /**
     * @param map number
     * @return a set of entities in current map
     */
    public Set<Integer> getEntitiesInMap(int map) {
        return entitiesByMap.get(map);
    }


    /**
     * Move entity to current position, leaving old relations if goes out of range
     *
     * @param player     player id
     * @param previusPos previus position in case its moving, empty if is a new position
     */
    public void movePlayer(int player, Optional<WorldPos> previusPos) {
        WorldPos actualPos = E(player).getWorldPos();
        previusPos.ifPresent(it -> {
            if (it.equals(actualPos)) {
                return;
            }
            //create footprint
            final int footprintId = getServer().getWorld().create();
            E(footprintId).footprintEntityId(player);
            E(footprintId).worldPosMap(it.map);
            E(footprintId).worldPosX(it.x);
            E(footprintId).worldPosY(it.y);
            E(footprintId).footprintTimestamp(TimeUtils.millis());
            entitiesFootprints.computeIfAbsent(player, (playerId) -> new HashSet<>()).add(footprintId);

            if (it.map != actualPos.map) {
                getEntitiesInMap(it.map).remove(player);
            }
            if (nearEntities.containsKey(player)) {
                Set<Integer> near = new HashSet<>(nearEntities.get(player));
                near.forEach(nearEntity -> removeNearEntity(player, nearEntity));
            }
        });
        updateEntity(player);
    }

    /**
     * Remove entity from map and unlink near entities
     *
     * @param entity
     */
    public void removeEntity(int entity) {
        final E e = E(entity);
        if (e == null || !e.hasWorldPos()) {
            return;
        }
        final WorldPos worldPos = e.getWorldPos();
        int map = worldPos.map;
        // remove from near entities
        nearEntities.computeIfPresent(entity, (player, removeFrom) -> {
            removeFrom.forEach(nearEntity -> {
                unlinkEntities(nearEntity, entity);
            });
            return null;
        });
        entitiesByMap.get(map).remove(entity);
    }

    /**
     * Add entity to map and calculate near entities
     *
     * @param player
     */
    public void updateEntity(int player) {
        int map = E(player).getWorldPos().map;
        Set<Integer> entities = entitiesByMap.computeIfAbsent(map, (it) -> new HashSet<>());
        entities.add(player);
        entities.stream()
                .filter(entity -> entity != player)
                .forEach(entity -> {
                    addNearEntities(player, entity);
                });
    }


    /**
     * Link entity1 and entity2 if they are in near range
     *
     * @param entity1
     * @param entity2
     */
    private void addNearEntities(int entity1, int entity2) {
        int distance = WorldUtils(getServer().getWorld()).distance(E(entity2).getWorldPos(), E(entity1).getWorldPos());
        if (distance >= 0 && distance <= MAX_DISTANCE) {
            linkEntities(entity1, entity2);
            linkEntities(entity2, entity1);
        }
    }

    /**
     * Unlink entities if they are out of range
     *
     * @param player1
     * @param player2
     */
    private void removeNearEntity(int player1, int player2) {
        int distance = WorldUtils(getServer().getWorld()).distance(E(player2).getWorldPos(), E(player1).getWorldPos());
        if (distance < 0 || distance > MAX_DISTANCE) {
            unlinkEntities(player1, player2);
            unlinkEntities(player2, player1);
        }
    }

    /**
     * Unlink entities
     *
     * @param entity1
     * @param entity2
     */
    private void unlinkEntities(int entity1, int entity2) {
        if (nearEntities.containsKey(entity1)) {
            nearEntities.get(entity1).remove(entity2);
        }
        // always notify that this entity is not longer in range
        getServer().getWorldManager().sendEntityRemove(entity1, entity2);
    }


    /**
     * Link entities
     *
     * @param entity1
     * @param entity2
     */
    private void linkEntities(int entity1, int entity2) {
        Set<Integer> near = nearEntities.computeIfAbsent(entity1, (i) -> new HashSet<>());
        if (near.add(entity2)) {
            EntityUpdate update = new EntityUpdate(entity2, WorldUtils(getServer().getWorld()).getComponents(entity2), new Class[0]);
            getServer().getWorldManager().sendEntityUpdate(entity1, update);
        }
    }

    /**
     * @param mapNumber
     * @return corresponding Map
     */
    public shared.model.map.Map get(int mapNumber) {
        return maps.get(mapNumber);
    }

    public Map<Integer, Set<Integer>> getEntitiesFootprints() {
        return entitiesFootprints;
    }
}
