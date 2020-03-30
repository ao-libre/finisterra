package game.systems.physics;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import game.systems.world.WorldSystem;
import movement.Destination;
import physics.AOPhysics;
import position.WorldPos;
import position.WorldPosOffsets;
import shared.model.map.Tile;

import static com.artemis.E.E;

@Wire
public class MovementSystem extends IteratingSystem {

    private WorldSystem worldSystem;

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
            player.worldPosOffsets();

            if (movePlayer(player)) {
                WorldPos worldPos = player.getWorldPos();
                WorldPos dest = player.movementCurrent().pos;
                worldPos.x = dest.x;
                worldPos.y = dest.y;
                worldPos.map = dest.map;
                player.getWorldPosOffsets().x = 0;
                player.getWorldPosOffsets().y = 0;
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
        AOPhysics.Movement movementDir = AOPhysics.Movement.values()[destination.dir];
        WorldPosOffsets offsets = player.getWorldPosOffsets();
        switch (movementDir) {
            case DOWN:
                offsets.y += delta;
                break;
            case LEFT:
                offsets.x -= delta;
                break;
            case RIGHT:
                offsets.x += delta;
                break;
            case UP:
                offsets.y -= delta;
                break;
        }
        player.headingCurrent(worldSystem.getHeading(movementDir));
        adjustPossiblePos(player);
        return offsets.x % 1 == 0 && offsets.y % 1 == 0;
    }

    private void adjustPossiblePos(E player) {
        player.getWorldPosOffsets().x = MathUtils.clamp(player.getWorldPosOffsets().x, -1, 1);
        player.getWorldPosOffsets().y = MathUtils.clamp(player.getWorldPosOffsets().y, -1, 1);
    }

}
