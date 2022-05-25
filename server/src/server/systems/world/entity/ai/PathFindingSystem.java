package server.systems.world.entity.ai;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;
import com.esotericsoftware.minlog.Log;
import component.entity.character.Character;
import component.entity.character.states.Heading;
import component.entity.character.states.Immobile;
import component.entity.character.status.Health;
import component.entity.npc.AIMovement;
import component.entity.npc.NPC;
import component.entity.npc.OriginPos;
import component.entity.world.Footprint;
import component.movement.Destination;
import component.physics.AOPhysics;
import component.position.WorldPos;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.utils.UpdateTo;
import server.utils.WorldUtils;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;
import shared.util.MapHelper;
import shared.util.Pair;

import java.util.*;

import static component.physics.AOPhysics.Movement.*;
import static server.utils.WorldUtils.WorldUtils;

public class PathFindingSystem extends IntervalIteratingSystem {

    private static final int MAX_DISTANCE_TARGET = 10;
    private MapSystem mapSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private WorldEntitiesSystem worldEntitiesSystem;

    private HashMap<Integer, AStarMap> maps = new HashMap<>();

    ComponentMapper<NPC> mNPC;
    ComponentMapper<Character> mCharacter;
    ComponentMapper<OriginPos> mOriginPos;
    ComponentMapper<WorldPos> mWorldPos;
    ComponentMapper<Health> mHealth;
    ComponentMapper<Heading> mHeading;
    ComponentMapper<Immobile> mImmobile;

    public PathFindingSystem(float interval) {
        super(Aspect.all(NPC.class, WorldPos.class, AIMovement.class).exclude(Character.class, Footprint.class, Immobile.class), interval);
    }

    private AStarMap updateMap(Integer map) {
        Set<Integer> entitiesInMap = mapSystem.getEntitiesInMap(map);
        if (entitiesInMap.stream().noneMatch(e -> mCharacter.has(e))) {
            return maps.get(map);
        }
        // TODO can we update on each move instead of create all again?
        AStarMap starMap = createStarMap(map);
        maps.put(map, starMap);
        return starMap;
    }

    @Override
    protected void begin() {
        mapSystem.getMaps().forEach(this::updateMap);
    }

    @Override
    protected void process(int entityId) {
        WorldPos origin = mWorldPos.get(entityId);
        if (!maps.containsKey(origin.map)) {
            return;
        }
        AStarMap aStarMap = maps.get(origin.map);
        Optional<Integer> target1 = findTarget(origin);
        WorldPos targetPos = target1.map(mWorldPos::get).orElse(mOriginPos.get(entityId).toWorldPos());
        if (targetPos.equals(mWorldPos.get(entityId))) {
            return;
        } else if (target1.isEmpty() && WorldUtils.WorldUtils(world).distance(origin, mOriginPos.get(entityId).toWorldPos()) < 10) {
            return;
        }
        makeYourMove(entityId, origin, targetPos, aStarMap);
    }

    private void makeYourMove(int entityId, WorldPos origin, WorldPos targetPos, AStarMap map) {
        boolean originWasWall = map.getNodeAt(origin.x, origin.y).isWall;
        boolean targetWasWall = map.getNodeAt(targetPos.x, targetPos.y).isWall;
        map.getNodeAt(origin.x, origin.y).isWall = false;
        map.getNodeAt(targetPos.x, targetPos.y).isWall = false;
        AStartPathFinding aStartPathFinding = new AStartPathFinding(map);
        Node from = aStartPathFinding.map.getNodeAt(origin.x, origin.y);
        Node nextNode = aStartPathFinding.findNextNode(origin, targetPos);
        move(entityId, from, nextNode);
        map.getNodeAt(origin.x, origin.y).isWall = originWasWall;
        map.getNodeAt(targetPos.x, targetPos.y).isWall = targetWasWall;
    }

    private void move(int entityId, Node from, Node nextNode) {
        if (nextNode == null) {
            Log.info("Cant find next node");
            return;
        }
        if (nextNode.x - from.x > 0) {
            // move right
            moveEntity(entityId, RIGHT);
        } else if (nextNode.x - from.x < 0) {
            // move left
            moveEntity(entityId, LEFT);
        } else if (nextNode.y - from.y > 0) {
            // move south
            moveEntity(entityId, DOWN);
        } else if (nextNode.y - from.y < 0) {
            // move north
            moveEntity(entityId, UP);
        }
    }

    private void moveEntity(int entityId, AOPhysics.Movement mov) {
        WorldUtils worldUtils = WorldUtils(world);
        int headingMov = worldUtils.getHeading(mov);
        boolean headingChanged = headingMov != mHeading.get(entityId).getCurrent();
        if (headingChanged) {
            mHeading.get(entityId).setCurrent(headingMov);
            EntityUpdate update = EntityUpdateBuilder.of(entityId).withComponents(mHeading.get(entityId)).build();
            entityUpdateSystem.add(update, UpdateTo.ALL);
        }

        WorldPos worldPos = mWorldPos.get(entityId);
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = worldUtils.getNextPos(worldPos, mov);

        Map map = mapSystem.getMap(nextPos.map);
        boolean blocked = mapSystem.getHelper().isBlocked(map, nextPos);
        boolean occupied = mapSystem.getHelper().hasEntity(mapSystem.getNearEntities(entityId), nextPos);
        Tile tile = MapHelper.getTile(map, nextPos);
        if (mImmobile.has(entityId) || blocked || occupied || (tile != null && tile.getTileExit() != null)) {
            nextPos = oldPos;
        }

        mWorldPos.get(entityId).setWorldPos(nextPos);

        mapSystem.movePlayer(entityId, Optional.of(oldPos));

        // notify near users
        if (nextPos != oldPos) {
            worldEntitiesSystem.notifyUpdate(entityId, new MovementNotification(entityId, new Destination(nextPos, mov.ordinal())));
        }
    }

    private Optional<Integer> findTarget(WorldPos worldPos) {
        // @todo AspectSubscriptionManager podría retornar un BitSet
        // O habría que encapsular esto de alguna manera para que pueda ser reutilizado
        IntBag entityBag = world.getAspectSubscriptionManager().get(Aspect.all(Character.class)).getEntities();

        // Pasamos a BitSet para poder usar stream API
        BitSet entitySet = new BitSet();
        for (int i = 0; i < entityBag.size(); i++) {
            entitySet.set(entityBag.get(i));
        }

        return entitySet.stream()
                .filter(mWorldPos::has)
                .filter(entityId -> mHealth.get(entityId).getMin() != 0)
                .mapToObj(entityId -> new Pair<>(entityId, WorldUtils(world).distance(mWorldPos.get(entityId), worldPos)))
                .filter(pair -> pair.getValue() < MAX_DISTANCE_TARGET && pair.getValue() >= 0)
                .min(Comparator.comparingInt(Pair::getValue))
                .map(Pair::getKey);
    }

    private AStarMap createStarMap(int map) {
        Map realMap = mapSystem.getMap(map);
        int height = realMap.getHeight();
        int width = realMap.getWidth();

        AStarMap aMap = new AStarMap(width, height);
        MapHelper helper = mapSystem.getHelper();
        Set<Integer> entitiesInMap = mapSystem.getEntitiesInMap(map);
        for (int x = 1; x < width; x++) {
            for (int y = 1; y < height; y++) {
                Node nodeAt = aMap.getNodeAt(x, y);
                Tile tile = realMap.getTile(x, y);
                nodeAt.isWall = tile == null || helper.isBlocked(realMap, x, y) || helper.hasEntity(entitiesInMap, new WorldPos(x, y, map));
            }
        }

        return aMap;
    }

    public AStarMap getMap(int map) {
        return maps.get(map);
    }
}
