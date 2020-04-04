package game.systems.physics;

import component.camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import game.systems.input.InputSystem;
import game.utils.AOKeys;
import game.utils.AlternativeKeys;
import component.physics.AOPhysics;

import static com.artemis.E.E;

public class PlayerInputSystem extends IteratingSystem {


    public PlayerInputSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class));
    }

    @Override
    protected void process(int entityId) {
        E player = E(entityId);
        AOPhysics aoPhysics = player.getAOPhysics();
        boolean isWriting = InputSystem.alternativeKeys && player.isWriting();
        final int moveUp = InputSystem.alternativeKeys ? AlternativeKeys.MOVE_UP : AOKeys.MOVE_UP;
        final int moveDown = InputSystem.alternativeKeys ? AlternativeKeys.MOVE_DOWN : AOKeys.MOVE_DOWN;
        final int moveLeft = InputSystem.alternativeKeys ? AlternativeKeys.MOVE_LEFT : AOKeys.MOVE_LEFT;
        final int moveRight = InputSystem.alternativeKeys ? AlternativeKeys.MOVE_RIGHT : AOKeys.MOVE_RIGHT;
        move(aoPhysics, AOPhysics.Movement.UP, !isWriting && Gdx.input.isKeyPressed(moveUp));
        move(aoPhysics, AOPhysics.Movement.DOWN, !isWriting && Gdx.input.isKeyPressed(moveDown));
        move(aoPhysics, AOPhysics.Movement.LEFT, !isWriting && Gdx.input.isKeyPressed(moveLeft));
        move(aoPhysics, AOPhysics.Movement.RIGHT, !isWriting && Gdx.input.isKeyPressed(moveRight));
    }

    private void move(AOPhysics aoPhysics, AOPhysics.Movement movement, boolean moving) {
        if (moving) {
            if (!aoPhysics.intentions.contains(movement)) {
                aoPhysics.addIntention(movement);
            }
        } else {
            aoPhysics.removeIntention(movement);
        }
    }
}
