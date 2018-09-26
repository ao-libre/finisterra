package ar.com.tamborindeguy.client.systems.physics;

import ar.com.tamborindeguy.client.handlers.MapHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.systems.interactions.MeditateSystem;
import ar.com.tamborindeguy.client.utils.ClientMapUtils;
import ar.com.tamborindeguy.model.map.Tile;
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
import physics.AOPhysics;
import position.Pos2D;
import position.WorldPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
public class MovementProcessorSystem extends IteratingSystem {

    private static int requestNumber;
    public static java.util.Map<Integer, MovementRequest> requests = new ConcurrentHashMap<>();

    public MovementProcessorSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class,
                WorldPos.class, Pos2D.class));
    }

    @Override
    protected void process(int entity) {
        E player = E(entity);
        final AOPhysics phys = player.getAOPhysics();
        final WorldPos pos = player.getWorldPos();
        Optional<AOPhysics.Movement> movementIntention = phys.getMovementIntention();
        if (!player.hasDestination()) {
            movementIntention.ifPresent(movement -> {
                player.headingCurrent(getHeading(movement));
                WorldPos expectedPos = Util.getNextPos(pos, movement);
                Set<Integer> nearEntities = GameScreen.getEntities();
                nearEntities.remove(entity);
                nearEntities.forEach(near -> Log.debug("Validating entity: " + near + " is not occuping the position"));
                boolean blocked = MapUtils.isBlocked(MapHandler.get(expectedPos.map), expectedPos);
                boolean occupied = MapUtils.hasEntity(nearEntities, expectedPos);
                boolean valid = !(blocked ||
                        occupied ||
                        player.hasImmobile());
                MovementRequest request = new MovementRequest(++requestNumber, movement, valid);
                requests.put(requestNumber, request);
                GameScreen.getClient().sendToAll(request);
                if (valid) { // Prediction
                    ClientMapUtils.updateTile(Tile.EMPTY_INDEX, pos);
                    player.destinationWorldPos(expectedPos);
                    player.destinationDir(movement);
                    stopMeditating(player);
                }
            });
        }
    }

    private int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }

    private void stopMeditating(E player) {
        player.removeMeditating();
        MeditateSystem.stopMeditating(player);
    }

    public static WorldPos getPosition(WorldPos worldPos) {
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
        requests.remove(requestNumber);
        E(GameScreen.getPlayer()).worldPosMap(destination.map);
        E(GameScreen.getPlayer()).worldPosY(destination.y);
        E(GameScreen.getPlayer()).worldPosX(destination.x);
        if (ClientMapUtils.changeMap(E(GameScreen.getPlayer()), destination)) {
            return;
        }
        ClientMapUtils.updateTile(GameScreen.getPlayer(), destination);
    }


}
