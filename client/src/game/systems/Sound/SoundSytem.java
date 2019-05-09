package game.systems.Sound;

import Sound.AOSound;
import com.artemis.Aspect;
import com.artemis.E;
import static com.artemis.E.E;
import com.artemis.systems.IteratingSystem;
import game.handlers.SoundsHandler;

import java.util.HashMap;
import java.util.Map;


class SoundIndexPair {
    int SoundID;
    long SoundIndex;

    public SoundIndexPair(int ID, long Index){
        SoundID = ID;
        SoundIndex = Index;
    }
}

public class SoundSytem extends IteratingSystem {

    public SoundSytem() {super(Aspect.all(AOSound.class));}

    private Map<Integer, SoundIndexPair> sounds = new HashMap<>();

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        E entity = E(entityId);

        AOSound sound = entity.getAOSound();

        long soundIndex = SoundsHandler.playSound(sound.soundID, sound.shouldLoop);

        sounds.put(entityId,new SoundIndexPair(sound.soundID, soundIndex));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);

        SoundIndexPair soundIndexPair = sounds.get(entityId);

        if (soundIndexPair != null) {
            SoundsHandler.stopSound(sounds.get(entityId).SoundID, sounds.get(entityId).SoundIndex);
        }
    }

    @Override
    protected void process(int entityId) {

    }
}
