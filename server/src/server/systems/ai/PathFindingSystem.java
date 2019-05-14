package server.systems.ai;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.EBag;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.minlog.Log;
import entity.character.Character;
import entity.world.Footprint;
import movement.Destination;
import physics.AOPhysics;
import position.WorldPos;
import server.systems.IntervalFluidIteratingSystem;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import server.utils.WorldUtils;
import shared.model.map.Map;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.util.MapHelper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static physics.AOPhysics.Movement.*;
import static server.utils.WorldUtils.WorldUtils;

public class PathFindingSystem extends IntervalFluidIteratingSystem {

    public static final int MATRIX_SIZE = 15;

    public PathFindingSystem(float interval) {
        super(Aspect.all(WorldPos.class).exclude(Character.class, Footprint.class), interval);
    }

    @Override
    protected void process(E e) {
        Log.info("Preprocess failing");
        WorldPos origin = e.getWorldPos();
        findTarget(origin).ifPresent(target -> {
            WorldPos targetPos = target.getWorldPos();
            AStarMap map = new AStarMap(MATRIX_SIZE, MATRIX_SIZE);
            fillBlocks(e, map);
            Log.info(map.toString());
            Log.info("Looking for target " + target.id() + (target.hasName() ? target.getName().text : ""));
            int mid = (MATRIX_SIZE / 2) + 1;
            int targetX = mid + targetPos.x - origin.x;
            int targetY = mid + targetPos.y - origin.y;
            Log.info("targetX: " + targetX + " - targetY: " + targetY);
            Log.info("Origin:" + origin);
            Log.info("Target:" + targetPos);

            AStartPathFinding aStartPathFinding = new AStartPathFinding(map);
            Node from = aStartPathFinding.map.getNodeAt(mid, mid);
            Node nextNode = aStartPathFinding
                    .findNextNode(new Vector2(mid, mid), new Vector2(targetX, targetY));
            move(e, from, nextNode);
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

        MapManager mapManager = world.getSystem(MapManager.class);
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
        worldManager.notifyUpdate(entityId, new EntityUpdate(entityId, new Component[]{player.getHeading()}, new Class[0])); // is necessary?
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
                    return distance < MATRIX_SIZE && distance > 0;
                })
                .findFirst();
    }

    private void fillBlocks(E e, AStarMap map) {
        WorldPos origin = e.getWorldPos();
        MapManager mapManager = world.getSystem(MapManager.class);
        int offset = MATRIX_SIZE / 2 + 1;
        MapHelper helper = mapManager.getHelper();
        Map realMap = helper.getMap(origin.map);
        for (int x = 0; x < MATRIX_SIZE; x++) {
            for (int y = 0; y < MATRIX_SIZE; y++) {
                int mX = origin.x - offset + x;
                int mY = y - offset + origin.y;
                Node nodeAt = map.getNodeAt(x, y);
                nodeAt.isWall = helper.isBlocked(realMap, mX, mY) || helper.hasEntity(mapManager.getNearEntities(e.id()), origin);
            }
        }

    }
}
