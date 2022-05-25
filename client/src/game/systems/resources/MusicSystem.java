package game.systems.resources;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

/**
 * @todo Si la música está deshabilitada, en playMusic() debería igualmente actualizarse la música actual
 * @todo toggle() no le hace caso a isDisabled()
 * @todo los timers de fadeIn y fadeOut deberían cancelarse en caso de que se cambie la música o se deshabilite el sistema
 */
public class MusicSystem {

    private static final float MUSIC_FADE_STEP = 0.1f;

    @Wire private DefaultAOAssetManager assetManager;

    private float volume = 1.0f;
    private boolean disabled;
    private Music currentMusic;
    private int currentMusicID = -1;

    public void playMusic(int musicID) {
        playMusic(musicID, false);
    }

    public void playMusic(int musicID, boolean fadeIn) {
        playMusic(musicID, fadeIn, true);
    }

    public void playMusic(int musicID, boolean fadeIn, boolean loop) {
        if (isDisabled()) return;
        if (musicID != currentMusicID) {
            // paramos la música actual
            if (currentMusic != null) {
                // todo: if (fadeIn) fadeout
                currentMusic.stop();
            }
            // cargamos la nueva música
            currentMusic = assetManager.getMusic(musicID);
            if (currentMusic == null) {
                Log.warn("MusicSystem", "Couldn't get music ID: " + musicID);
                currentMusicID = -1;
                return;
            }
            currentMusicID = musicID;
        }
        if (currentMusic != null) {
            // seteamos el looping
            currentMusic.setLooping(loop);
            // reproducimos
            if (!(currentMusic.isPlaying())) {
                if (fadeIn) {
                    fadeInMusic(MUSIC_FADE_STEP);
                } else {
                    currentMusic.play();
                }
            }
        }
    }

    public void playMusic() {
        if (currentMusic != null && !isDisabled()) currentMusic.play();
    }

    public void pauseMusic() {
        if (currentMusic != null) currentMusic.pause();
    }

    public void stopMusic() {
        if (currentMusic != null) currentMusic.stop();
    }

    public boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public void toggle() {
        if (isPlaying()) {
            currentMusic.pause();
        } else {
            currentMusic.play();
        }
    }

    public void fadeInMusic(float step) {
        if (currentMusic != null && !isDisabled()) {
            currentMusic.setVolume(0.0f);
            currentMusic.play();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (currentMusic == null) {
                        Log.warn("MusicSystem", "Tried to fade null music reference.");
                        this.cancel();
                        return;
                    }
                    if (currentMusic.getVolume() < volume) {
                        currentMusic.setVolume(currentMusic.getVolume() + step);
                    } else {
                        this.cancel();
                    }
                }
            }, 0.0f, 0.5f);
        }
    }

    public void fadeOutMusic(float step) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (currentMusic == null) {
                    Log.warn("MusicSystem", "Tried to fade null music reference.");
                    this.cancel();
                    return;
                }
                if (currentMusic.getVolume() > 0.0f) {
                    currentMusic.setVolume(currentMusic.getVolume() - step);
                } else {
                    currentMusic.stop();
                    this.cancel();
                }
            }
        }, 0.0f, 0.5f);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (disabled) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    public float getVolume() {
        return volume;
    }

    // Control de volumen
    public void setVolume(float volume) {
        this.volume = MathUtils.clamp(volume, 0.0f, 1.0f);
        if (currentMusic != null) currentMusic.setVolume(volume);
    }

    public void volumeDown() {
        setVolume(volume - 0.1f);
    }

    public void volumeUp() {
        setVolume(volume + 0.1f);
    }


    // Manejo de MIDIs de acá para abajo. @todo Implementar

    public void setMIDIVolume(float volume) {
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
