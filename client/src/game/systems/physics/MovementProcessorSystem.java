package game.systems.physics;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.camera.Focused;
import component.entity.character.states.Heading;
import component.movement.Destination;
import component.physics.AOPhysics;
import component.position.WorldPos;
import game.systems.PlayerSystem;
import game.systems.network.ClientSystem;
import game.systems.resources.MapSystem;
import game.systems.world.NetworkedEntitySystem;
import org.jetbrains.annotations.NotNull;
import shared.model.map.Map;
import shared.model.map.WorldPosition;
import shared.network.interaction.MeditateRequest;
import shared.network.movement.MovementRequest;
import shared.util.WorldPosConversion;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
public class MovementProcessorSystem extends IteratingSystem {

    private final java.util.Map<Integer, MovementRequest> requests = new ConcurrentHashMap<>();
    private int requestNumber;
    private NetworkedEntitySystem networkedEntitySystem;
    private ClientSystem clientSystem;
    private PlayerSystem playerSystem;

    public MovementProcessorSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class,
                WorldPos.class));
    }

    public WorldPos getDelta(@NotNull WorldPos worldPos) {
        WorldPos correctPos = new WorldPos(worldPos.x, worldPos.y, worldPos.map);
        requests.values().stream().filter(it -> it.valid).forEach(request -> {
            WorldPos nextPos = WorldPosConversion.getNextPos(correctPos, AOPhysics.Movement.values()[request.movement]);
            correctPos.x = nextPos.x;
            correctPos.y = nextPos.y;
            correctPos.map = nextPos.map;
        });
        return correctPos;
    }

    public void validateRequest(int requestNumber, WorldPos destination) {
        WorldPos predicted = requests.get(requestNumber).predicted;
        requests.remove(requestNumber);
        if (!predicted.equals(destination)) {
            E player = playerSystem.get();
            if (!player.hasMovement()) {
                return;
            }
            player.getMovement().destinations.clear();
            WorldPos worldPos = player.getWorldPos();
            if (player.hasWorldPosOffsets()) {
                player.getWorldPosOffsets().x = 0;
                player.getWorldPosOffsets().y = 0;
            }
            if (!worldPos.equals(destination)) {
                player.getMovement().add(new Destination(destination, getDir(worldPos, destination).ordinal()));
            }
        }
    }

    private static AOPhysics.Movement getDir(WorldPos worldPos, WorldPos destination) {
        if (worldPos.x < destination.x) {
            return AOPhysics.Movement.RIGHT;
        } else if (worldPos.x > destination.x) {
            return AOPhysics.Movement.LEFT;
        } else if (worldPos.y < destination.y) {
            return AOPhysics.Movement.DOWN;
        } else if (worldPos.y > destination.y) {
            return AOPhysics.Movement.UP;
        }
        return AOPhysics.Movement.DOWN;
    }

    @Override
    protected void process(int entity) {
        E player = E(entity);
        final WorldPos pos = player.getWorldPos();
        final AOPhysics phys = player.getAOPhysics();
        Optional<AOPhysics.Movement> movementIntention = phys.getMovementIntention();
        if (!player.movementHasMovements()) {
            if (movementIntention.isPresent()) {
                AOPhysics.Movement movement = movementIntention.get();
                player.headingCurrent(getHeading(movement));
                WorldPos expectedPos = WorldPosConversion.getNextPos(pos, movement);
                Set<Integer> nearEntities = networkedEntitySystem.getAll();
                nearEntities.remove(entity);
                Map map = MapSystem.get(expectedPos.map);
                boolean blocked = MapSystem.getHelper().isBlocked(map, expectedPos);
                boolean occupied = MapSystem.getHelper().hasEntity(nearEntities, expectedPos);
                boolean valid = !(blocked ||
                        occupied ||
                        player.hasImmobile());
                boolean tileExit = MapSystem.getHelper().hasTileExit(map, expectedPos);
                if (tileExit) {
                    WorldPosition tileExitPos = map.getTile(expectedPos.x, expectedPos.y).getTileExit();
                    expectedPos = new WorldPos(tileExitPos.getX(), tileExitPos.getY(), tileExitPos.getMap());
                }
                MovementRequest request = new MovementRequest(++requestNumber, valid ? expectedPos : pos, movement.ordinal(), valid);
                if (requests.containsValue(request)) {
                    // ignore multiple requests with same direction & prediction
                    return;
                }
                requests.put(requestNumber, request);
                clientSystem.send(request);
                if (valid) { // Prediction
                    Destination destination = new Destination(expectedPos, movement.ordinal());
                    player.movementAdd(destination);
                    if (player.isMeditating()) {
                        clientSystem.send(new MeditateRequest());
                    }
                }
            }
        }
    }

    private int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }

}
