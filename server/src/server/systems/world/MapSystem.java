package server.systems.world;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.TimeUtils;
import component.entity.world.Footprint;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.ServerSystem;
import server.systems.world.entity.factory.ComponentSystem;
import server.systems.world.entity.factory.EntityFactorySystem;
import server.utils.UpdateTo;
import shared.model.map.Tile;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;
import shared.util.MapHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static shared.util.MapHelper.CacheStrategy.NEVER_EXPIRE;

/**
 * Logic regarding maps, contains information about entities in each map, and how are they related.
 */
public class MapSystem extends PassiveSystem {

    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private EntityFactorySystem entityFactorySystem;
    private ComponentSystem componentSystem;
    private ServerSystem serverSystem;

    ComponentMapper<WorldPos> mWorldPos;
    ComponentMapper<Footprint> mFootprint;

    private MapHelper helper;
    private Map<Integer, Set<Integer>> nearEntities = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesByMap = new ConcurrentHashMap<>();
    private Map<Integer, Set<Integer>> entitiesFootprints = new ConcurrentHashMap<>();

    public MapSystem() {
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
                .filter(mWorldPos::has)
                .filter(e -> mWorldPos.get(e).equals(pos))
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
     * @param previousPos previous position in case its moving, empty if is a new position
     */
    public void movePlayer(int player, Optional<WorldPos> previousPos) {
        WorldPos actualPos = mWorldPos.get(player);
        previousPos.ifPresent(it -> {
            if (it.equals(actualPos)) {
                return;
            }
            //create footprint
            final int footprintId = world.create();
            Footprint footprint = mFootprint.create(footprintId);
            footprint.setEntityId(player);
            mWorldPos.create(footprintId).setWorldPos(it);
            footprint.setTimestamp(TimeUtils.millis());
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
        if (mWorldPos.has(entity)) {
            return;
        }
        final WorldPos worldPos = mWorldPos.get(entity);
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
        WorldPos pos = mWorldPos.get(player);
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
        WorldPos worldPos1 = mWorldPos.get(entity1);
        WorldPos worldPos2 = mWorldPos.get(entity2);
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
        if (!helper.isNear(mWorldPos.get(player1), mWorldPos.get(player2))) {
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
                EntityUpdate update = EntityUpdateBuilder.of(entity2).withComponents(componentSystem.getComponents(entity2, ComponentSystem.Visibility.CLIENT_PUBLIC)).build();
                entityUpdateSystem.add(entity1, update, UpdateTo.ENTITY);
            }
        }
    }

    public Map<Integer, Set<Integer>> getEntitiesFootprints() {
        return entitiesFootprints;
    }
}
