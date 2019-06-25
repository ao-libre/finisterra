package game.systems.physics;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import game.utils.WorldUtils;
import movement.Destination;
import physics.AOPhysics;
import position.WorldPos;
import shared.model.map.Tile;

import static com.artemis.E.E;

@Wire
public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Aspect.all(WorldPos.class, AOPhysics.class));
    }

    @Override
    protected void process(int entity) {
        E player = E(entity);
        if (player.movementHasMovements()) {

            if (!player.isMoving()) {
                player.aOSound();
                player.aOSoundSoundID(23).aOSoundShouldLoop(true);
            }

            player.moving(true);

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
        } else {

            if (player.isMoving()) {
                player.removeAOSound();
            }

            player.moving(false);
        }
    }

    private boolean movePlayer(E player) {
        Destination destination = player.movementCurrent();
        float velocity = player.getAOPhysics().getVelocity();
        float delta = world.getDelta() * velocity / Tile.TILE_PIXEL_HEIGHT;
        switch (destination.dir) {
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
