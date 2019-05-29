package server.systems.manager;

import com.artemis.E;
import com.badlogic.gdx.utils.TimeUtils;
import position.WorldPos;
import server.core.Server;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.util.MapHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

/**
 * Logic regarding maps, contains information about entities in each map, and how are they related.
 */
public class MapManager extends DefaultManager {

    public static final int MAX_DISTANCE = 15;
    private HashMap<Integer, shared.model.map.Map> maps;
    private final MapHelper helper;
    //    public int mapEntity;
    private Map<Integer, Set<Integer>> nearEntities = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesByMap = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesFootprints = new ConcurrentHashMap<>();

    public MapManager(Server server, HashMap<Integer, shared.model.map.Map> maps) {
        super(server);
        helper = MapHelper.instance();
        this.maps = maps;
    }

    public Set<Integer> getMaps() {
        return maps.keySet();
    }

    public shared.model.map.Map getMap(int map) {
        return maps.get(map);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    public void postInitialize() {
        // create NPCs
        maps.forEach((num, map) -> {
            initTiles(num, map);
        });
    }

    private void initTiles(int num, shared.model.map.Map map) {
        Tile[][] mapTiles = map.getTiles();
        for (int x = 0; x < mapTiles.length; x++) {
            for (int y = 0; y < mapTiles[x].length; y++) {
                if (mapTiles[x][y] != null) {
                    int npcIndex = mapTiles[x][y].getNpcIndex();
                    WorldPos pos = new WorldPos(x, y, num);
                    if (npcIndex > 0) {
                        createNPC(npcIndex, pos);
                    }
                    int objIndex = mapTiles[x][y].getObjIndex();
                    if (objIndex > 0) {
                        int objCount = mapTiles[x][y].getObjCount();
                        createObject(objIndex, objCount, pos);
                    }
                }
            }
        }
    }

    private void createObject(int objIndex, int objCount, WorldPos pos) {
        world.getSystem(WorldManager.class).createObject(objIndex, objCount, pos);
    }

    private void createNPC(int npcIndex, WorldPos pos) {
        world.getSystem(WorldManager.class).createNPC(npcIndex, pos);
    }

    public MapHelper getHelper() {
        return helper;
    }

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
        return entitiesByMap.computeIfAbsent(map, i -> new HashSet<>());
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
                .forEach(entity -> addNearEntities(player, entity));
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
            EntityUpdate update = EntityUpdateBuilder.of(entity2).withComponents(WorldUtils(world).getComponents(entity2)).build();
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
