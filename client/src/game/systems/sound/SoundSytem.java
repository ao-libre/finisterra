package game.systems.sound;

import component.sound.AOSound;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import game.systems.PlayerSystem;
import game.systems.resources.SoundsSystem;
import game.systems.world.WorldSystem;
import component.position.WorldPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private SoundsSystem soundsSystem;

    private WorldSystem worldSystem;
    private PlayerSystem playerSystem;

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

        long soundIndex = soundsSystem.playSound(sound.id, sound.shouldLoop);
        sounds.put(entityId, new SoundIndexPair(sound.id, soundIndex));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        SoundIndexPair soundInstance = sounds.get(entityId);
        if (soundInstance != null) {
            soundsSystem.stopSound(soundInstance.soundID, soundInstance.soundIndex);
        }
    }

    @Override
    protected void process(int entityId) {
        int mainPlayer = playerSystem.get().id();
        WorldPos playerPos = E(mainPlayer).getWorldPos();
        // check distance to entity if has worldpos and update volume
        E soundEntity = E(entityId);
        if (soundEntity.hasWorldPos() || soundEntity.hasRef()) {
            Optional.ofNullable(soundEntity.hasWorldPos() ? soundEntity.getWorldPos() : getRefPos(soundEntity.refId())).ifPresent(soundPos -> {
                float distance = worldSystem.distance(soundPos, playerPos);
                float distanceX = worldSystem.getDistanceX(soundPos, playerPos);
                if (sounds.containsKey(entityId)) {
                    SoundIndexPair soundIndexPair = sounds.get(entityId);
                    soundsSystem.updatePan(soundIndexPair.soundID, soundIndexPair.soundIndex, distanceX == 0 ? distanceX : MathUtils.clamp(1 / distanceX, -1, 1), MathUtils.clamp(1 / distance, -1, 1));
                }
            });
            if (!soundEntity.getAOSound().shouldLoop) {
                soundEntity.clearTime(soundsSystem.getDuration(soundEntity.aOSoundId()));
            }
        }

    }

    private WorldPos getRefPos(int refId) {
        WorldPos pos = null;
        if (E(refId) != null && E(refId).hasWorldPos()) {
            pos = E(refId).getWorldPos();
        }
        return pos;
    }
}
