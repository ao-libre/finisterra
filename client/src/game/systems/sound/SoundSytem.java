package game.systems.sound;

import sound.AOSound;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import game.handlers.SoundsHandler;
import game.screens.GameScreen;
import game.utils.WorldUtils;
import position.WorldPos;

import java.util.HashMap;
import java.util.Map;

import static com.artemis.E.E;


class SoundIndexPair {
    int soundID;
    long soundIndex;

    SoundIndexPair(int ID, long Index) {
        this.soundID = ID;
        this.soundIndex = Index;
    }
}

@Wire
public class SoundSytem extends IteratingSystem {

    public static float volume = 1.0f;
    private final Map<Integer, SoundIndexPair> sounds;
    private SoundsHandler soundsHandler;

    public SoundSytem() {
        super(Aspect.all(AOSound.class));
        this.sounds = new HashMap<>();
    }

    public void setVolume(float volume) {
        SoundSytem.volume = volume;
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);

        E entity = E(entityId);
        AOSound sound = entity.getAOSound();

        long soundIndex = soundsHandler.playSound(sound.soundID, sound.shouldLoop);
        sounds.put(entityId, new SoundIndexPair(sound.soundID, soundIndex));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);

        SoundIndexPair soundInstance = sounds.get(entityId);
        if (soundInstance != null) {
            soundsHandler.stopSound(soundInstance.soundID, soundInstance.soundIndex);
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
                    soundsHandler.updatePan(soundIndexPair.soundID, soundIndexPair.soundIndex, distanceX == 0 ? distanceX : MathUtils.clamp(1 / distanceX, -1, 1), MathUtils.clamp(1 / distance, -1, 1));
                }
            }
        }
    }
}
