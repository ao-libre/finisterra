package ar.com.tamborindeguy.client.systems.physics;

import ar.com.tamborindeguy.client.handlers.MapHandler;
import ar.com.tamborindeguy.client.managers.WorldManager;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.utils.ClientMapUtils;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.network.interaction.MeditateRequest;
import ar.com.tamborindeguy.network.movement.MovementRequest;
import ar.com.tamborindeguy.util.MapUtils;
import ar.com.tamborindeguy.util.Util;
import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import entity.Heading;
import movement.Destination;
import physics.AOPhysics;
import position.Pos2D;
import position.WorldPos;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
public class MovementProcessorSystem extends IteratingSystem {

    public static java.util.Map<Integer, MovementRequest> requests = new ConcurrentHashMap<>();
    private static int requestNumber;

    public MovementProcessorSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class,
                WorldPos.class, WorldPos.class));
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
            E player = E(GameScreen.getPlayer());
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
                boolean blocked = MapUtils.isBlocked(MapHandler.get(expectedPos.map), expectedPos);
                boolean occupied = MapUtils.hasEntity(nearEntities, expectedPos);
                boolean valid = !(blocked ||
                        occupied ||
                        player.hasImmobile());
                MovementRequest request = new MovementRequest(++requestNumber, valid ? expectedPos : pos, movement, valid);
                requests.put(requestNumber, request);
                GameScreen.getClient().sendToAll(request);
                if (valid) { // Prediction
                    ClientMapUtils.updateTile(Tile.EMPTY_INDEX, pos);
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
