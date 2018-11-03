package ar.com.tamborindeguy.client.systems.physics;

import ar.com.tamborindeguy.client.utils.WorldUtils;
import ar.com.tamborindeguy.model.map.Tile;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import movement.Destination;
import physics.AOPhysics;
import position.WorldPos;

import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Aspect.all(WorldPos.class, WorldPos.class, AOPhysics.class));
    }

    @Override
    protected void process(int entity) {
        E player = E(entity);
        if (player.movementHasMovements()) {
            if (movePlayer(player)) {
                WorldPos worldPos = player.getWorldPos();
                WorldPos dest = player.movementCurrent().worldPos;
                worldPos.x = dest.x;
                worldPos.y = dest.y;
                worldPos.map = dest.map;
                worldPos.offsetX = 0;
                worldPos.offsetY = 0;
                player.movementCompleteCurrent();
            }
        }
        final AOPhysics phys = player.getAOPhysics();
        Optional<AOPhysics.Movement> movementIntention = phys.getMovementIntention();
        player.moving(player.movementHasMovements() || movementIntention.isPresent());
    }

    private boolean movePlayer(E player) {
        Destination destination = player.movementCurrent();
        float delta = world.getDelta() * AOPhysics.WALKING_VELOCITY / Tile.TILE_PIXEL_HEIGHT;
        switch (destination.dir) {
            default:
            case DOWN:
                player.getWorldPos().offsetY += delta;
                break;
            case LEFT:
                player.getWorldPos().offsetX -= delta;
                break;
            case RIGHT:
                player.getWorldPos().offsetX += delta;
                break;
            case UP:
                player.getWorldPos().offsetY -= delta;
                break;
        }
        player.headingCurrent(WorldUtils.getHeading(destination.dir));
        adjustPossiblePos(player);
        return player.getWorldPos().offsetX % 1 == 0 && player.getWorldPos().offsetY % 1 == 0;
    }

    private void adjustPossiblePos(E player) {
        player.getWorldPos().offsetX = MathUtils.clamp(player.getWorldPos().offsetX, -1, 1);
        player.getWorldPos().offsetY = MathUtils.clamp(player.getWorldPos().offsetY, -1, 1);
    }

}
