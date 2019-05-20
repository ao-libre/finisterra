package server.systems.ai;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.EBag;
import com.esotericsoftware.minlog.Log;
import entity.character.Character;
import entity.character.states.Immobile;
import entity.world.Footprint;
import movement.Destination;
import physics.AOPhysics;
import position.WorldPos;
import server.systems.IntervalFluidIteratingSystem;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import server.utils.WorldUtils;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.util.MapHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static physics.AOPhysics.Movement.*;
import static server.utils.WorldUtils.WorldUtils;
import static shared.network.notifications.EntityUpdate.EntityUpdateBuilder.*;

public class PathFindingSystem extends IntervalFluidIteratingSystem {

    private HashMap<Integer, AStarMap> maps = new HashMap<>();

    public PathFindingSystem(float interval) {
        super(Aspect.all(WorldPos.class).exclude(Character.class, Footprint.class, Immobile.class), interval);
    }

    private MapManager getMapManager() {
        return world.getSystem(MapManager.class);
    }

    private void updateMap(Integer map) {
        if (getMapManager().getEntitiesInMap(map).size() == 0) {
            return;
        }
        maps.put(map, createStarMap(map));
    }

    @Override
    protected void begin() {
        getMapManager().getMaps().forEach(map -> updateMap(map));
    }

    @Override
    protected void process(E e) {
        WorldPos origin = e.getWorldPos();
        if (!maps.containsKey(origin.map)) {
            return;
        }
        Optional<E> target1 = findTarget(origin);
        Log.info("Path finding has target: " + target1.isPresent());
        target1.ifPresent(target -> {
            WorldPos targetPos = target.getWorldPos();
            Log.info("Looking for target " + target.id() + (target.hasName() ? target.getName().text : ""));
            Log.info("Origin:" + origin);
            Log.info("Target:" + targetPos);

            AStarMap map = maps.get(origin.map);
            boolean originWasWall = map.getNodeAt(origin.x, origin.y).isWall;
            boolean targetWasWall = map.getNodeAt(targetPos.x, targetPos.y).isWall;
            map.getNodeAt(origin.x, origin.y).isWall = false;
            map.getNodeAt(targetPos.x, targetPos.y).isWall = false;
            AStartPathFinding aStartPathFinding = new AStartPathFinding(map);
            Node from = aStartPathFinding.map.getNodeAt(origin.x, origin.y);
            Node nextNode = aStartPathFinding.findNextNode(origin, targetPos);
            move(e, from, nextNode);
            map.getNodeAt(origin.x, origin.y).isWall = originWasWall;
            map.getNodeAt(targetPos.x, targetPos.y).isWall = targetWasWall;
        });


    }

    private void move(E e, Node from, Node nextNode) {
        if (nextNode == null) {
            Log.info("Cant find next node");
            return;
        }
        int entityId = e.id();
        String text = e.hasName() ? e.getName().text : "NO NAME: " + entityId;
        if (nextNode.x - from.x > 0) {
            // move right
            moveEntity(entityId, RIGHT);
            Log.info(text + " AI MOVE RIGHT");
        } else if (nextNode.x - from.x < 0) {
            // move left
            moveEntity(entityId, LEFT);
            Log.info(text + " AI MOVE LEFT");
        } else if (nextNode.y - from.y > 0) {
            // move south
            moveEntity(entityId, DOWN);
            Log.info(text + " AI MOVE DOWN");
        } else {
            // move north
            moveEntity(entityId, UP);
            Log.info("AI MOVE UP");
        }
    }

    private void moveEntity(int entityId, AOPhysics.Movement mov) {
        E player = E.E(entityId);

        WorldUtils worldUtils = WorldUtils(world);
        player.headingCurrent(worldUtils.getHeading(mov));

        WorldPos worldPos = player.getWorldPos();
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = worldUtils.getNextPos(worldPos, mov);

        MapManager mapManager = getMapManager();
        Map map = mapManager.get(nextPos.map);
        boolean blocked = mapManager.getHelper().isBlocked(map, nextPos);
        boolean occupied = mapManager.getHelper().hasEntity(mapManager.getNearEntities(entityId), nextPos);
        if (player.hasImmobile() || blocked || occupied) {
            nextPos = oldPos;
        }

        player.worldPosMap(nextPos.map);
        player.worldPosX(nextPos.x);
        player.worldPosY(nextPos.y);

        mapManager.movePlayer(entityId, Optional.of(oldPos));

        WorldManager worldManager = world.getSystem(WorldManager.class);
        // notify near users
        EntityUpdate update = of(entityId).withComponents(player.getHeading()).build();
        worldManager.notifyUpdate(entityId, update); // is necessary?
        if (nextPos != oldPos) {
            worldManager.notifyUpdate(entityId, new MovementNotification(entityId, new Destination(nextPos, mov)));
        }
    }

    private Optional<E> findTarget(WorldPos worldPos) {
        Set<E> all = new HashSet<>();
        EBag es = E.withAspect(Aspect.all(Character.class));
        es.forEach(all::add);
        return all.stream()
                .filter(E::hasWorldPos)
                .filter(e -> {
                    int distance = WorldUtils(world).distance(e.getWorldPos(), worldPos);
                    return distance < 15 && distance > 0;
                })
                .findFirst();
    }

    private AStarMap createStarMap(int map) {
        MapManager mapManager = getMapManager();
        Map realMap = mapManager.get(map);
        int height = realMap.MAX_MAP_SIZE_HEIGHT;
        int width = realMap.MAX_MAP_SIZE_WIDTH;

        AStarMap aMap = new AStarMap(width, height);
        MapHelper helper = mapManager.getHelper();
        Set<Integer> entitiesInMap = mapManager.getEntitiesInMap(map);
        for (int x = 1; x < width; x++) {
            for (int y = 1; y < height; y++) {
                Node nodeAt = aMap.getNodeAt(x, y);
                Tile tile = realMap.getTile(x, y);
                nodeAt.isWall = tile == null || helper.isBlocked(realMap, x, y) || helper.hasEntity(entitiesInMap, new WorldPos(x, y, map));
            }
        }

        return aMap;
    }
}
