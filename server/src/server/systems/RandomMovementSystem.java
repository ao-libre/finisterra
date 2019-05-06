package server.systems;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import map.Cave;
import movement.Destination;
import movement.RandomMovement;
import physics.AOPhysics;
import position.WorldPos;
import server.core.Server;
import server.utils.WorldUtils;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.util.MapUtils;

import java.util.*;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

public class RandomMovementSystem extends IteratingSystem {
    private static final List<AOPhysics.Movement> VALUES =
            Collections.unmodifiableList(Arrays.asList(AOPhysics.Movement.values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    private Server server;

    public RandomMovementSystem(Server server) {
        super(Aspect.all(RandomMovement.class));
        this.server = server;
    }

    public static Optional<AOPhysics.Movement> randomMovement() {
        int index = RANDOM.nextInt(SIZE * 50);
        return Optional.ofNullable(VALUES.size() > index ? VALUES.get(index) : null);
    }

    public Server getServer() {
        return server;
    }

    @Override
    protected void process(int entityId) {
        Optional<AOPhysics.Movement> randomMovement = randomMovement();
        randomMovement.ifPresent(mov -> {
            // update server entity
            E player = E(entityId);

            WorldUtils worldUtils = WorldUtils(getServer().getWorld());
            player.headingCurrent(worldUtils.getHeading(mov));

            WorldPos worldPos = player.getWorldPos();
            WorldPos oldPos = new WorldPos(worldPos);
            WorldPos nextPos = worldUtils.getNextPos(worldPos, mov);
            Cave cave = E(getServer().getMapManager().mapEntity).getCave();
            boolean blocked = cave.isBlocked(nextPos.x, nextPos.y);
            boolean occupied = MapUtils.hasEntity(getServer().getMapManager().getNearEntities(entityId), nextPos);
            if (player.hasImmobile() || blocked || occupied) {
                nextPos = oldPos;
            }

            player.worldPosMap(nextPos.map);
            player.worldPosX(nextPos.x);
            player.worldPosY(nextPos.y);

            getServer().getMapManager().movePlayer(entityId, Optional.of(oldPos));

            // notify near users
            getServer().getWorldManager().notifyUpdate(entityId, new EntityUpdate(entityId, new Component[]{player.getHeading()}, new Class[0])); // is necessary?
            if (nextPos != oldPos) {
                getServer().getWorldManager().notifyUpdate(entityId, new MovementNotification(entityId, new Destination(nextPos, mov)));
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
