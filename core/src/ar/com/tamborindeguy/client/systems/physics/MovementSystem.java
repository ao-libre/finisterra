package ar.com.tamborindeguy.client.systems.physics;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.model.map.Tile;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import movement.Destination;
import physics.AOPhysics;
import position.Pos2D;
import position.WorldPos;

import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Aspect.all(WorldPos.class, Pos2D.class, AOPhysics.class));
    }

    @Override
    protected void process(int entity) {
        E player = E(entity);
        WorldPos pos = player.getWorldPos();
        if (entity == GameScreen.getPlayer()) {
            pos = MovementProcessorSystem.getPosition(pos);
        }
        if (player.movementHasMovements()) {
            if (movePlayer(player)) {
                if (entity != GameScreen.getPlayer()) {
                    WorldPos dest = player.movementCurrent().worldPos;
                    player.getWorldPos().x = dest.x;
                    player.getWorldPos().y = dest.y;
                    player.getWorldPos().map = dest.map;
                }
                player.movementCompleteCurrent();
            }
        }
        final AOPhysics phys = player.getAOPhysics();
        Optional<AOPhysics.Movement> movementIntention = phys.getMovementIntention();
        player.moving(player.movementHasMovements() || movementIntention.isPresent());
    }

    private boolean movePlayer(E player) {
        Destination destination = player.movementCurrent();
        Pos2D pos2D = player.getPos2D();
        float delta = world.getDelta() * AOPhysics.WALKING_VELOCITY / Tile.TILE_PIXEL_HEIGHT;
        switch (destination.dir) {
            default:
            case DOWN:
                pos2D.y += delta;
                break;
            case LEFT:
                pos2D.x -= delta;
                break;
            case RIGHT:
                pos2D.x += delta;
                break;
            case UP:
                pos2D.y -= delta;
                break;
        }

        adjustPossiblePos(pos2D, destination.worldPos, destination.dir);
        return pos2D.x % 1 == 0 && pos2D.y % 1 == 0;
    }

    private void adjustPossiblePos(Pos2D possiblePos, WorldPos destination, AOPhysics.Movement dir) {
        int newY = (int) possiblePos.y;
        int newX = (int) possiblePos.x;
        switch (dir) {
            case LEFT:
                if (newX < destination.x) {
                    possiblePos.x = destination.x;
                }
                break;
            case RIGHT:
                if (newX == destination.x) {
                    possiblePos.x = destination.x;
                }
                break;
            case UP:
                if (newY < destination.y) {
                    possiblePos.y = destination.y;
                }
                break;
            case DOWN:
                if (newY == destination.y) {
                    possiblePos.y = destination.y;
                }
                break;
        }
    }

}
