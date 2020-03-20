package game.systems.physics;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.google.common.collect.Lists;
import game.managers.AOInputProcessor;
import game.utils.AOKeys;
import game.utils.AlternativeKeys;
import physics.AOPhysics;

import java.util.ArrayList;

import static com.artemis.E.E;

public class PlayerInputSystem extends IteratingSystem {


    public PlayerInputSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class));
    }

    @Override
    protected void process(int entityId) {
        E player = E(entityId);
        AOPhysics aoPhysics = player.getAOPhysics();
        boolean isWriting = AOInputProcessor.alternativeKeys && player.isWriting();
        for (AOPhysics.Movement value : AOPhysics.Movement.values()) {
            move(aoPhysics, value, !isWriting && isMoving(value));
        }
    }

    private boolean isMoving(AOPhysics.Movement movement) {
        int code = getKeyCode(movement);
        boolean keyPressed = Gdx.input.isKeyPressed(code);

        ArrayList<Controller> controllers = Lists.newArrayList(Controllers.getControllers().toArray(Controller.class));
        boolean controllerMoving = controllers.stream().anyMatch(controller -> isMoving(controller, movement));
        return keyPressed || controllerMoving;
    }

    private int getKeyCode(AOPhysics.Movement movement) {
        switch (movement) {
            case UP:
                return AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_UP : AOKeys.MOVE_UP;
            case RIGHT:
                return AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_RIGHT : AOKeys.MOVE_RIGHT;
            case DOWN:
                return AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_DOWN : AOKeys.MOVE_DOWN;
            case LEFT:
                return AOInputProcessor.alternativeKeys ? AlternativeKeys.MOVE_LEFT : AOKeys.MOVE_LEFT;
        }
        return 0;
    }

    private boolean isMoving(Controller controller, AOPhysics.Movement movement) {
        float axis = 0;
        switch (movement) {
            case UP:
            case DOWN:
                axis = controller.getAxis(1);
                break;
            case LEFT:
            case RIGHT:
                axis = controller.getAxis(0);
                break;
        }

        boolean result = false;
        switch (movement) {
            case UP:
            case LEFT:
                result = axis < -0.5f;
                break;
            case DOWN:
            case RIGHT:
                result = axis > 0.5f;
                break;
        }

        return result;
    }

    public void move(int entityId, AOPhysics.Movement movement) {
        E player = E(entityId);
        AOPhysics aoPhysics = player.getAOPhysics();
        move(aoPhysics, movement, true);
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
