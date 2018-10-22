package ar.com.tamborindeguy.systems;

import ar.com.tamborindeguy.manager.MapManager;
import ar.com.tamborindeguy.manager.WorldManager;
import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.movement.MovementNotification;
import ar.com.tamborindeguy.network.movement.MovementResponse;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.util.MapUtils;
import ar.com.tamborindeguy.utils.WorldUtils;
import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import movement.Destination;
import movement.RandomMovement;
import physics.AOPhysics;
import position.WorldPos;

import java.util.*;

import static com.artemis.E.E;

public class RandomMovementSystem extends IteratingSystem {
    private static final List<AOPhysics.Movement> VALUES =
            Collections.unmodifiableList(Arrays.asList(AOPhysics.Movement.values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Optional<AOPhysics.Movement> randomMovement()  {
        int index = RANDOM.nextInt(SIZE * 50);
        return Optional.ofNullable(VALUES.size() > index ? VALUES.get(index) : null);
    }

    public RandomMovementSystem() {
        super(Aspect.all(RandomMovement.class));
    }

    @Override
    protected void process(int entityId) {
        Optional<AOPhysics.Movement> randomMovement = randomMovement();
        randomMovement.ifPresent(mov -> {
            // update server entity
            E player = E(entityId);

            player.headingCurrent(WorldUtils.getHeading(mov));

            WorldPos worldPos = player.getWorldPos();
            WorldPos oldPos = new WorldPos(worldPos);
            WorldPos nextPos = WorldUtils.getNextPos(worldPos, mov);
            boolean blocked = MapUtils.isBlocked(MapManager.get(nextPos.map), nextPos);
            boolean occupied = MapUtils.hasEntity(MapManager.getNearEntities(entityId), nextPos);
            if (player.hasImmobile() || blocked || occupied) {
                nextPos = oldPos;
            }

            player.worldPosMap(nextPos.map);
            player.worldPosX(nextPos.x);
            player.worldPosY(nextPos.y);

            MapManager.movePlayer(entityId, Optional.of(oldPos));

            // notify near users
            WorldManager.notifyUpdate(entityId, new EntityUpdate(entityId, new Component[] {player.getHeading()}, new Class[0])); // is necessary?
            if (nextPos != oldPos) {
                WorldManager.notifyUpdate(entityId, new MovementNotification(entityId, new Destination(nextPos, mov)));
            }
        });
    }

    private List<AOPhysics.Movement> otherMovements(Optional<AOPhysics.Movement> movement) {
        return movement.isPresent() ? getOther(movement.get()) : VALUES;
    }

    private List<AOPhysics.Movement> getOther(AOPhysics.Movement movement) {
        List<AOPhysics.Movement> result = new ArrayList<>();
        result.addAll(VALUES);
        result.remove(movement);
        return result;
    }
}
