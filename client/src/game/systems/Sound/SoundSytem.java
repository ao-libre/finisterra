package game.systems.Sound;

import Sound.AOSound;
import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;

public class SoundSytem extends IteratingSystem {

    public SoundSytem() {super(Aspect.all(AOSound.class));}

    @Override
    public void inserted(IntBag entities) {
        super.inserted(entities);

    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);

    }

    @Override
    protected void process(int entityId) {

    }
}
