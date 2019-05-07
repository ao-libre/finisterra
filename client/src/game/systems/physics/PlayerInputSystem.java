package game.systems.physics;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import game.handlers.SoundsHandler;
import game.managers.AOInputProcessor;
import game.utils.AOKeys;
import game.utils.AlternativeKeys;
import physics.AOPhysics;

import static com.artemis.E.E;

public class PlayerInputSystem extends IteratingSystem {


    public PlayerInputSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class));
    }

    @Override
    protected void process(int entityId) {
        E player = E(entityId);
        AOPhysics aoPhysics = player.getAOPhysics();
        boolean isWriting = player.isWriting();
        final int moveUp = AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_UP : AOKeys.MOVE_UP;
        final int moveDown = AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_DOWN : AOKeys.MOVE_DOWN;
        final int moveLeft = AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_LEFT : AOKeys.MOVE_LEFT;
        final int moveRight = AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_RIGHT : AOKeys.MOVE_RIGHT;
        move(aoPhysics, AOPhysics.Movement.UP, !isWriting && Gdx.input.isKeyPressed(moveUp));
        move(aoPhysics, AOPhysics.Movement.DOWN, !isWriting && Gdx.input.isKeyPressed(moveDown));
        move(aoPhysics, AOPhysics.Movement.LEFT, !isWriting && Gdx.input.isKeyPressed(moveLeft));
        move(aoPhysics, AOPhysics.Movement.RIGHT, !isWriting && Gdx.input.isKeyPressed(moveRight));
    }

    private void move(AOPhysics aoPhysics, AOPhysics.Movement movement, boolean moving) {
        if (moving) {
            if (!aoPhysics.intentions.contains(movement)) {
                aoPhysics.addIntention(movement);
                SoundsHandler.playSound(23);
            }
        } else if (!moving) {
            aoPhysics.removeIntention(movement);
        }
    }

}
