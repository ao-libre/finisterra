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

    float volume = 1.0f;
    boolean disabled;

    public long playSound(Integer soundID, boolean loop) {
        if (disabled) return -1;

        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Tried to play sound ID: " + soundID + ", but it was not loaded.");
            return -1;
        }

        if (!loop) {
            return sound.play(volume);
        } else {
            return sound.loop(volume);
        }
    }

    public long playSound(Integer soundID) {
        return playSound(soundID, false);
    }

    public void updateVolume(Integer soundId, long soundIndex, float volume) {
        Sound sound = assetManager.getSound(soundId);
        if (sound != null) {
            sound.setVolume(soundIndex, volume * this.volume);
        }
    }

    public void updatePan(Integer soundId, long soundIndex, float pan, float volume) {
        Sound sound = assetManager.getSound(soundId);
        if (sound != null) {
            sound.setPan(soundIndex, pan, volume * this.volume);
        }
    }

    public void stopSound(Integer soundID) {
        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Tried to stop sound ID: " + soundID + ", but it was not loaded.");
            return;
        }
        sound.stop();
    }

    public void stopSound(Integer soundID, long soundIndex) {
        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Tried to stop sound ID: " + soundID + ", but it was not loaded.");
            return;
        }
        sound.stop(soundIndex);
    }

    // seconds
    public float getDuration(int soundID) {
        Sound sound = assetManager.getSound(soundID);
        return sound instanceof OpenALSound ? ((OpenALSound) sound).duration() : 0;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        // todo: actualizar el volumen de todos los sonidos actuales
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
