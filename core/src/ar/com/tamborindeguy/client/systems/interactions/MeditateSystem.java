package ar.com.tamborindeguy.client.systems.interactions;

import ar.com.tamborindeguy.client.handlers.ParticlesHandler;
import ar.com.tamborindeguy.client.utils.Keys;
import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import entity.character.states.Writing;
import movement.Moving;

import static com.artemis.E.E;

public class MeditateSystem extends IteratingSystem {

    private static int MEDITATE_NW_FX = 1;
    private static int MEDITATE_13_FX = 2;
    private static int MEDITATE_25_FX = 3;

    public MeditateSystem() {
        super(Aspect.all(Focused.class).exclude(Writing.class, Moving.class));
    }

    public static void stopMeditating(E entity) {
        entity.removeFX();
    }

    @Override
    protected void process(int entityId) {
        if (Gdx.input.isKeyJustPressed(Keys.MEDITATE)) {
            boolean meditating = E(entityId).hasMeditating();
            if (meditating) {
                stopMeditating(E(entityId));
            } else {
                E(entityId).fX().fXAdd(ParticlesHandler.getParticle(MEDITATE_NW_FX));
            }
            if (meditating) {
                E(entityId).removeMeditating();
            } else {
                E(entityId).meditating();
            }
        }
    }

}
