package game.systems.physics;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import entity.character.states.Heading;
import game.handlers.MapHandler;
import game.managers.WorldManager;
import game.screens.GameScreen;
import movement.Destination;
import physics.AOPhysics;
import position.WorldPos;
import shared.model.map.Map;
import shared.model.map.WorldPosition;
import shared.network.interaction.MeditateRequest;
import shared.network.movement.MovementRequest;
import shared.util.Util;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
public class MovementProcessorSystem extends IteratingSystem {

    private static java.util.Map<Integer, MovementRequest> requests = new ConcurrentHashMap<>();
    private static int requestNumber;
    private WorldManager worldManager;

    public MovementProcessorSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class,
                WorldPos.class));
    }

    public static WorldPos getDelta(WorldPos worldPos) {
        WorldPos correctPos = new WorldPos(worldPos.x, worldPos.y, worldPos.map);
        requests.values().stream().filter(it -> it.valid).forEach(request -> {
            WorldPos nextPos = Util.getNextPos(correctPos, AOPhysics.Movement.values()[request.movement]);
            correctPos.x = nextPos.x;
            correctPos.y = nextPos.y;
            correctPos.map = nextPos.map;
        });
        return correctPos;
    }

    public static void validateRequest(int requestNumber, WorldPos destination) {
        WorldPos predicted = requests.get(requestNumber).predicted;
        requests.remove(requestNumber);
        if (!predicted.equals(destination)) {
            E player = E.E(GameScreen.getPlayer());
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
                WorldPos expectedPos = Util.getNextPos(pos, movement);
                Set<Integer> nearEntities = worldManager.getEntities();
                nearEntities.remove(entity);
                Map map = MapHandler.get(expectedPos.map);
                boolean blocked = MapHandler.getHelper().isBlocked(map, expectedPos);
                boolean occupied = MapHandler.getHelper().hasEntity(nearEntities, expectedPos);
                boolean valid = !(blocked ||
                        occupied ||
                        player.hasImmobile());
                boolean tileExit = MapHandler.getHelper().hasTileExit(map, expectedPos);
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
                GameScreen.getClient().sendToAll(request);
                if (valid) { // Prediction
                    Destination destination = new Destination(expectedPos, movement.ordinal());
                    player.movementAdd(destination);
                    if (player.isMeditating()) {
                        GameScreen.getClient().sendToAll(new MeditateRequest());
                    }
                }
            }
        }
    }

    private int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }

}
