package server.systems.manager;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.TimeUtils;
import component.position.WorldPos;
import server.systems.EntityFactorySystem;
import server.systems.ServerSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.UpdateTo;
import shared.model.map.Tile;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;
import shared.util.MapHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.artemis.E.E;
import static shared.util.MapHelper.CacheStrategy.NEVER_EXPIRE;

/**
 * Logic regarding maps, contains information about entities in each map, and how are they related.
 */
@Wire
public class MapManager extends DefaultManager {

    private WorldManager worldManager;
    private EntityUpdateSystem entityUpdateSystem;
    private EntityFactorySystem entityFactorySystem;
    private ComponentManager componentManager;
    private ServerSystem serverSystem;

    private MapHelper helper;
    private Map<Integer, Set<Integer>> nearEntities = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesByMap = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesFootprints = new ConcurrentHashMap<>();

    public MapManager() {
        helper = MapHelper.instance(NEVER_EXPIRE);
    }

    public Set<Integer> getMaps() {
        return helper.getMaps().keySet();
    }

    public shared.model.map.Map getMap(int map) {
        return helper.getMap(map);
    }

    @Override
    public void initialize() {
        super.initialize();
        // create NPCs
        helper.getMaps().forEach(this::initTiles);
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
                }
            }
        }
    }

    private void createObject(int objIndex, int objCount, WorldPos pos) {
        entityFactorySystem.createObject(objIndex, objCount, pos);
    }

    private void createNPC(int npcIndex, WorldPos pos) {
        entityFactorySystem.createNPC(npcIndex, pos);
    }

    public MapHelper getHelper() {
        return helper;
    }

    /**
     * @param entityId id
     * @return a set of near entities or empty
     */
    public Set<Integer> getNearEntities(int entityId) {
        Set<Integer> nearEntities = this.nearEntities.getOrDefault(entityId, ConcurrentHashMap.newKeySet());
        return nearEntities;
    }

    public Set<Integer> getEntities(WorldPos pos) {
        return entitiesByMap.get(pos.map)
                .stream()
                .map(E::E)
                .filter(E::hasWorldPos)
                .filter(e -> e.getWorldPos().equals(pos))
                .map(E::id)
                .collect(Collectors.toSet());
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
            final int footprintId = world.create();
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
     * Remove component.entity from map and unlink near entities
     *
     * @param entity id
     */
    void removeEntity(int entity) {
        final E e = E(entity);
        if (e == null || !e.hasWorldPos()) {
            return;
        }
        final WorldPos worldPos = e.getWorldPos();
        int map = worldPos.map;
        // remove from near entities
        nearEntities.computeIfPresent(entity, (player, removeFrom) -> {
            removeFrom.forEach(nearEntity -> unlinkEntities(nearEntity, entity));
            return null;
        });
        entitiesByMap.get(map).remove(entity);
    }

    /**
     * Add component.entity to map and calculate near entities
     *
     * @param player id
     */
    void updateEntity(int player) {
        WorldPos pos = E(player).getWorldPos();
        int map = pos.map;
        Set<Integer> entities = entitiesByMap.computeIfAbsent(map, (it) -> new HashSet<>());
        Set<Integer> candidates = new HashSet<>(entities);
        candidates.addAll(getNearMapsEntities(pos));
        entities.add(player);
        candidates.stream()
                .filter(entity -> entity != player)
                .forEach(entity -> addNearEntities(player, entity));
    }

    // TODO improve performance
    private Collection<? extends Integer> getNearMapsEntities(WorldPos pos) {
        Set<Integer> result = new HashSet<>();
        shared.model.map.Map map = helper.getMap(pos.map);
        if (pos.x > map.getWidth() / 2) {
            int rightMap = helper.getMap(MapHelper.Dir.RIGHT, map);
            result.addAll(getEntitiesInMap(rightMap));
            addNearCorners(pos, result, rightMap);
        } else {
            int leftMap = helper.getMap(MapHelper.Dir.LEFT, map);
            result.addAll(getEntitiesInMap(leftMap));
            addNearCorners(pos, result, leftMap);
        }
        addNearUpOrDown(pos, result, map);
        return result;
    }

    private void addNearUpOrDown(WorldPos pos, Set<Integer> result, shared.model.map.Map map) {
        if (pos.y > map.getHeight() / 2) {
            int bottom = helper.getMap(MapHelper.Dir.DOWN, map);
            if (bottom > 0) {
                result.addAll(getEntitiesInMap(bottom));
            }
        } else {
            int top = helper.getMap(MapHelper.Dir.UP, map);
            if (top > 0) {
                result.addAll(getEntitiesInMap(top));
            }
        }
    }

    private void addNearCorners(WorldPos pos, Set<Integer> result, int mapNumber) {
        if (mapNumber <= 0) {
            return;
        }
        shared.model.map.Map map = helper.getMap(mapNumber);
        addNearUpOrDown(pos, result, map);
    }


    /**
     * Link entity1 and entity2 if they are in near range
     *
     * @param entity1 id
     * @param entity2 id
     */
    private void addNearEntities(int entity1, int entity2) {
        WorldPos worldPos1 = E(entity2).getWorldPos();
        WorldPos worldPos2 = E(entity1).getWorldPos();
        if (helper.isNear(worldPos1, worldPos2)) {
            linkEntities(entity1, entity2);
            linkEntities(entity2, entity1);
        }
    }

    /**
     * Unlink entities if they are out of range
     *
     * @param player1 id
     * @param player2 id
     */
    private void removeNearEntity(int player1, int player2) {
        if (!helper.isNear(E(player2).getWorldPos(), E(player1).getWorldPos())) {
            unlinkEntities(player1, player2);
            unlinkEntities(player2, player1);
        }
    }

    /**
     * Unlink entities
     *
     * @param entity1 id
     * @param entity2 id
     */
    private void unlinkEntities(int entity1, int entity2) {
        if (nearEntities.containsKey(entity1)) {
            nearEntities.get(entity1).remove(entity2);
        }
        // always notify that this entity is not longer in range
        entityUpdateSystem.add(entity1, EntityUpdateBuilder.delete(entity2), UpdateTo.ENTITY);
    }


    /**
     * Link entities
     *
     * @param entity1 id
     * @param entity2 id
     */
    private void linkEntities(int entity1, int entity2) {
        Set<Integer> near = nearEntities.computeIfAbsent(entity1, (i) -> new HashSet<>());
        if (near.add(entity2)) {
            if (serverSystem.playerHasConnection(entity1)) { // if its a player or networked entity, send new entity
                EntityUpdate update = EntityUpdateBuilder.of(entity2).withComponents(componentManager.getComponents(entity2, ComponentManager.Visibility.CLIENT_PUBLIC)).build();
                entityUpdateSystem.add(entity1, update, UpdateTo.ENTITY);
            }
        }
    }

    public Map<Integer, Set<Integer>> getEntitiesFootprints() {
        return entitiesFootprints;
    }
}
