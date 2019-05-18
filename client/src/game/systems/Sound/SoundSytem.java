package game.systems.Sound;

import Sound.AOSound;
import com.artemis.Aspect;

import static com.artemis.E.E;

import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import game.handlers.SoundsHandler;
import game.screens.GameScreen;
import game.utils.WorldUtils;
import position.WorldPos;

import java.util.HashMap;
import java.util.Map;


class SoundIndexPair {
    int soundID;
    long soundIndex;

    SoundIndexPair(int ID, long Index){
        this.soundID = ID;
        this.soundIndex = Index;
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
        sounds.put(entityId, new SoundIndexPair(sound.soundID, soundIndex));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);

        SoundIndexPair soundInstance = sounds.get(entityId);
        if (soundInstance != null) {
            SoundsHandler.stopSound(soundInstance.soundID, soundInstance.soundIndex);
        }
    }

    @Override
    protected void process(int entityId) {
        int mainPlayer = GameScreen.getPlayer();
        if (entityId != mainPlayer) {
            // check distance to entity if has worldpos and update volume
            E soundEntity = E(entityId);
            if (soundEntity.hasWorldPos()) {
                WorldPos soundPos = soundEntity.getWorldPos();
                WorldPos playerPos = E(mainPlayer).getWorldPos();
                float distance = WorldUtils.distance(soundPos, playerPos);
                float distanceX = WorldUtils.getDistanceX(soundPos, playerPos);
                if (sounds.containsKey(entityId)) {
                    SoundIndexPair soundIndexPair = sounds.get(entityId);
                    SoundsHandler.updatePan(soundIndexPair.soundID, soundIndexPair.soundIndex, distanceX == 0 ? distanceX : MathUtils.clamp(1 / distanceX, -1, 1), MathUtils.clamp(1 / distance, -1, 1));
                }
            }
        }
    }
}
