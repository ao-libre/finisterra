package game.systems.resources;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

@Wire
public class MusicSystem extends PassiveSystem { // @todo revisar

    static private final float MUSIC_FADE_STEP = 0.01f;

    @Wire
    private DefaultAOAssetManager assetManager;

    private float volume = 1.0f;
    private boolean disableMusic;
    // Current music
    private Music currentMusic;
    private int currentMusicID = -1;

    public void playMusic() {
        if (currentMusic == null) {
            Log.warn("MusicSystem", "Error: tried to pause music index: " + currentMusicID + ", but it was not loaded.");
            return;
        }
        currentMusic.play();
    }

    public void playMusic(int musicID) {
        playMusic(musicID, true, false);
    }

    public void playMusic(int musicID, boolean loop, boolean restart) {
        if (!disableMusic) {
            if (musicID < 0) throw new IllegalArgumentException("musicID must be positive");

            if (musicID != currentMusicID) {
                // paramos la música
                if (currentMusic != null) {
                    currentMusic.stop();
                    // borramos las referencias
                    currentMusic = null;
                    currentMusicID = -1;
                }

                // cargamos la nueva música
                currentMusic = assetManager.getMusic(musicID);
                if (currentMusic == null) { // Error al cargar
                    Log.warn("MusicSystem", "Could not get music index: " + musicID);
                    return;
                }
                currentMusicID = musicID;
            }

            // reproducimos
            currentMusic.setVolume(volume);
            currentMusic.setLooping(loop);
            if (restart) currentMusic.stop();
            currentMusic.play();
        }
    }

    public void pauseMusic() {
        if (currentMusic == null) {
            Log.warn("MusicSystem", "Error: tried to pause music index: " + currentMusicID + ", but it was not loaded.");
            return;
        }
        currentMusic.pause();
    }

    public void stopMusic() {
        if (currentMusic == null) {
            Log.warn("MusicSystem", "Error: tried to stop music index: " + currentMusicID + ", but it was not loaded.");
            return;
        }
        currentMusic.stop();
    }

    public void toggle() {
        if (currentMusic.isPlaying()) {
            currentMusic.pause();
        } else {
            currentMusic.play();
        }
    }

    public void fadeInMusic(float fadeRate, float maxVol) {
        currentMusic.setVolume( 0f );
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (currentMusic == null) {
                    Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to fade music index: " + currentMusic + ", but it was not playing or loaded.");
                    return;
                }
                if (currentMusic.getVolume() < maxVol)
                    currentMusic.setVolume(currentMusic.getVolume() + MUSIC_FADE_STEP);
                else {
                    this.cancel();
                }
            }
        }, 0f, fadeRate);
    }

    public void fadeOutMusic( float fadeRate) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (currentMusic == null) {
                    Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to fade music index: " + currentMusic + ", but it was not playing or loaded.");
                    return;
                }
                if (currentMusic.getVolume() >= MUSIC_FADE_STEP)
                    currentMusic.setVolume(currentMusic.getVolume() - MUSIC_FADE_STEP);
                else {
                    currentMusic.stop();
                    this.cancel();
                }
            }
        }, 0f, fadeRate);
    }

    public boolean isDisableMusic(){
        return disableMusic;
    }
    public void setDisableMusic(boolean musicEnable){
        this.disableMusic = musicEnable;
    }

    public void volumeDown(float vol) {
        currentMusic.setVolume(currentMusic.getVolume() - vol);
    }

    public void volumeUp(float vol) {
        currentMusic.setVolume(currentMusic.getVolume() + vol);
    }

    public void setVolume(float volume) {
        this.volume = volume;
        try {
            MidiChannel[] channels = MidiSystem.getSynthesizer().getChannels();
            for (MidiChannel channel : channels) {
                if (channel != null) channel.controlChange(7, (int) (volume * 127));
            }
        } catch (MidiUnavailableException e) {
            Log.error("MusicSystem", "Error while setting volume", e);
        }
    }

    //TODO: MIDIs cant be faded at the moment
    public void playMIDI(int midiID) {
        Sequencer sequencer = assetManager.getMidi(midiID);

        if (sequencer == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to play midi index: " + midiID + ", but it was not loaded.");
            return;
        }

        sequencer.start();
    }

    public void stopMIDI(int midiID) {
        Sequencer sequencer = assetManager.getMidi(midiID);

        if (sequencer == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to play midi index: " + midiID + ", but it was not loaded.");
            return;
        }

        sequencer.stop();
    }
}
