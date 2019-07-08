package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import game.AOGame;
import game.systems.sound.SoundSytem;
import net.mostlyoriginal.api.system.core.PassiveSystem;


public class SoundsHandler extends PassiveSystem {

    private AOAssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();
    }

    public long playSound(Integer soundID, boolean loop) {
        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
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
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return;
        }

        sound.stop();
    }

    public void stopSound(Integer soundID, long soundIndex) {
        Sound sound = assetManager.getSound(soundID);
        if (sound == null) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return;
        }
        sound.stop(soundIndex);
    }

}
