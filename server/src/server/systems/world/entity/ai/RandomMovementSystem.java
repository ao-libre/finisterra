package server.systems.world.entity.ai;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import component.entity.character.states.Heading;
import component.entity.character.states.Immobile;
import component.movement.Destination;
import component.movement.RandomMovement;
import component.physics.AOPhysics;
import component.position.WorldPos;
import server.systems.network.EntityUpdateSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.utils.UpdateTo;
import server.utils.WorldUtils;
import shared.model.map.Map;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.*;

import static server.utils.WorldUtils.WorldUtils;

public class RandomMovementSystem extends IteratingSystem {

    private static final List<AOPhysics.Movement> VALUES =
            Collections.unmodifiableList(Arrays.asList(AOPhysics.Movement.values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    private MapSystem mapSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private EntityUpdateSystem entityUpdateSystem;

    ComponentMapper<Heading> mHeading;
    ComponentMapper<Immobile> mImmobile;
    ComponentMapper<WorldPos> mWorldPos;

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
        WorldUtils worldUtils = WorldUtils(world);
        mHeading.get(entityId).setCurrent(worldUtils.getHeading(mov));

        WorldPos worldPos = mWorldPos.get(entityId);
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = worldUtils.getNextPos(worldPos, mov);

        Map map = mapSystem.getMap(nextPos.map);
        boolean blocked = mapSystem.getHelper().isBlocked(map, nextPos);
        boolean occupied = mapSystem.getHelper().hasEntity(mapSystem.getNearEntities(entityId), nextPos);
        if (mImmobile.has(entityId) || blocked || occupied) {
            nextPos = oldPos;
        }

        worldPos.setWorldPos(nextPos);

        mapSystem.movePlayer(entityId, Optional.of(oldPos));

        // notify near users

        EntityUpdate update = EntityUpdateBuilder.of(entityId).withComponents(mHeading.get(entityId)).build();
        entityUpdateSystem.add(update, UpdateTo.ALL);
        if (nextPos != oldPos) {
            worldEntitiesSystem.notifyUpdate(entityId, new MovementNotification(entityId, new Destination(nextPos, mov.ordinal())));
        }
    }

}
