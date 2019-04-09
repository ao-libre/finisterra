package game.systems.physics;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import entity.character.states.Heading;
import game.managers.WorldManager;
import game.screens.GameScreen;
import game.systems.map.CaveSystem;
import movement.Destination;
import physics.AOPhysics;
import position.WorldPos;
import shared.network.interaction.MeditateRequest;
import shared.network.movement.MovementRequest;
import shared.util.MapUtils;
import shared.util.Util;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
public class MovementProcessorSystem extends IteratingSystem {

    private CaveSystem caveSystem;
    public static java.util.Map<Integer, MovementRequest> requests = new ConcurrentHashMap<>();
    private static int requestNumber;

    public MovementProcessorSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class,
                WorldPos.class));
    }

    public static WorldPos getDelta(WorldPos worldPos) {
        WorldPos correctPos = new WorldPos(worldPos.x, worldPos.y, worldPos.map);
        requests.values().stream().filter(it -> it.valid).forEach(request -> {
            WorldPos nextPos = Util.getNextPos(correctPos, request.movement);
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
            player.getMovement().destinations.clear();
            WorldPos worldPos = player.getWorldPos();
            worldPos.offsetY = 0;
            worldPos.offsetX = 0;
            if (!worldPos.equals(destination)) {
                player.getMovement().add(new Destination(destination, getDir(worldPos, destination)));
            }
        }
    }

    private static AOPhysics.Movement getDir(WorldPos worldPos, WorldPos destination) {
        if (worldPos.x < destination.x) {
            return AOPhysics.Movement.RIGHT;
        } else if (worldPos.x >  destination.x) {
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
                Set<Integer> nearEntities = WorldManager.getEntities();
                nearEntities.remove(entity);
                nearEntities.forEach(near -> Log.debug("Validating entity: " + near + " is not occuping the position"));

                boolean blocked = caveSystem.isBlocked(expectedPos.x, expectedPos.y); //MapUtils.isBlocked(MapHandler.get(expectedPos.map), expectedPos);
                boolean occupied = MapUtils.hasEntity(nearEntities, expectedPos);
                boolean valid = !(blocked ||
                        occupied ||
                        player.hasImmobile());
                MovementRequest request = new MovementRequest(++requestNumber, valid ? expectedPos : pos, movement, valid);
                if (requests.containsValue(request)) {
                    // ignore multiple requests with same direction & prediction
                    return;
                }
                requests.put(requestNumber, request);
                GameScreen.getClient().sendToAll(request);
                if (valid) { // Prediction
                    // ClientMapUtils.updateTile(Tile.EMPTY_INDEX, pos); // not used. TODO clean?
                    Destination destination = new Destination(expectedPos, movement);
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
