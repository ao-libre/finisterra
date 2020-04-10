package game.systems.resources;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALSound;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;
import game.systems.sound.SoundSytem;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class SoundsSystem extends PassiveSystem {

    @Wire
    private DefaultAOAssetManager assetManager;

    public long playSound(Integer soundID, boolean loop) {
        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return -1;
        }
        //TODO: it should be played with a global configurable volume
        if (!loop) {
            return sound.play(SoundSytem.volume);
        } else {
            return sound.loop(SoundSytem.volume);
        }
    }

    public void playSound(Integer soundID) {
        playSound(soundID, false);
    }

    public void updateVolume(Integer soundId, long soundIndex, float volume) {
        Sound sound = assetManager.getSound(soundId);
        if (sound != null) {
            sound.setVolume(soundIndex, volume * SoundSytem.volume);
        }
    }

    public void updatePan(Integer soundId, long soundIndex, float pan, float volume) {
        Sound sound = assetManager.getSound(soundId);
        if (sound != null) {
            sound.setPan(soundIndex, pan, volume * SoundSytem.volume);
        }
    }

    public void stopSound(Integer soundID) {
        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return;
        }

        sound.stop();
    }

    public void stopSound(Integer soundID, long soundIndex) {
        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return;
        }
        sound.stop(soundIndex);
    }

    // seconds
    public float getDuration(int soundID) {
        Sound sound = assetManager.getSound(soundID);
        return sound instanceof OpenALSound ? ((OpenALSound) sound).duration() : 0;
    }
}
