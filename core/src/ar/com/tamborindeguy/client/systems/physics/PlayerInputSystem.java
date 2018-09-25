package ar.com.tamborindeguy.client.systems.physics;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import physics.AOPhysics;

import static com.artemis.E.E;

public class PlayerInputSystem extends IteratingSystem {


    public PlayerInputSystem() {
        super(Aspect.all(Focused.class, AOPhysics.class));
    }

    @Override
    protected void process(int entityId) {
        AOPhysics aoPhysics = E(entityId).getAOPhysics();
        boolean isWriting = !E(entityId).hasCanWrite();
        move(aoPhysics, AOPhysics.Movement.UP, !isWriting && Gdx.input.isKeyPressed(Input.Keys.UP));
        move(aoPhysics, AOPhysics.Movement.DOWN, !isWriting && Gdx.input.isKeyPressed(Input.Keys.DOWN));
        move(aoPhysics, AOPhysics.Movement.LEFT, !isWriting && Gdx.input.isKeyPressed(Input.Keys.LEFT));
        move(aoPhysics, AOPhysics.Movement.RIGHT, !isWriting && Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    }

    public void move(AOPhysics aoPhysics, AOPhysics.Movement movement, boolean moving) {
        if (moving) {
            if (!aoPhysics.intentions.contains(movement)) {
                aoPhysics.addIntention(movement);
            }
        } else if (!moving) {
            aoPhysics.removeIntention(movement);
        }
    }

}
