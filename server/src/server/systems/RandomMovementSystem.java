package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.movement.Destination;
import component.movement.RandomMovement;
import component.physics.AOPhysics;
import component.position.WorldPos;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.UpdateTo;
import server.utils.WorldUtils;
import shared.model.map.Map;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.*;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

@Wire
public class RandomMovementSystem extends IteratingSystem {

    private static final List<AOPhysics.Movement> VALUES =
            Collections.unmodifiableList(Arrays.asList(AOPhysics.Movement.values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    private MapManager mapManager;
    private WorldManager worldManager;
    private EntityUpdateSystem entityUpdateSystem;

    public RandomMovementSystem() {
        super(Aspect.all(RandomMovement.class));
    }

    private static Optional<AOPhysics.Movement> randomMovement() {
        int index = RANDOM.nextInt(SIZE * 50);
        return Optional.ofNullable(VALUES.size() > index ? VALUES.get(index) : null);
    }

    @Override
    protected void process(int entityId) {
        Optional<AOPhysics.Movement> randomMovement = randomMovement();
        randomMovement.ifPresent(mov -> {
            // update server component.entity
            moveEntity(entityId, mov);
        });
    }

    private void moveEntity(int entityId, AOPhysics.Movement mov) {
        E player = E(entityId);

        WorldUtils worldUtils = WorldUtils(world);
        player.headingCurrent(worldUtils.getHeading(mov));

        WorldPos worldPos = player.getWorldPos();
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = worldUtils.getNextPos(worldPos, mov);

        Map map = mapManager.getMap(nextPos.map);
        boolean blocked = mapManager.getHelper().isBlocked(map, nextPos);
        boolean occupied = mapManager.getHelper().hasEntity(mapManager.getNearEntities(entityId), nextPos);
        if (player.hasImmobile() || blocked || occupied) {
            nextPos = oldPos;
        }

        player.worldPosMap(nextPos.map);
        player.worldPosX(nextPos.x);
        player.worldPosY(nextPos.y);

        mapManager.movePlayer(entityId, Optional.of(oldPos));

        // notify near users

        EntityUpdate update = EntityUpdateBuilder.of(entityId).withComponents(player.getHeading()).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);
        if (nextPos != oldPos) {
            worldManager.notifyUpdate(entityId, new MovementNotification(entityId, new Destination(nextPos, mov.ordinal())));
        }
    }

}
