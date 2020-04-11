package game.systems.resources;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

@Wire
public class MusicSystem extends PassiveSystem {

    public final static Music FIRSTBGM = Gdx.audio.newMusic(Gdx.files.internal("data/music/101.mp3"));
    public final static Music BACKGROUNDMUSIC = Gdx.audio.newMusic(Gdx.files.internal("data/music/1.mp3"));
    private static final float MUSIC_FADE_STEP = 0.01f;
    private static float volume = 1.0f;
    @Wire
    private DefaultAOAssetManager assetManager;

    public Music current;

    public static void setVolume(float volume) {
        MusicSystem.volume = volume;
        try {
            MidiChannel[] channels = MidiSystem.getSynthesizer().getChannels();
            for (MidiChannel channel : channels) {
                if (channel != null) channel.controlChange(7, (int) (volume * 127));
            }
        } catch (MidiUnavailableException e) {
            Log.error("Music System", "Error while setting volume", e);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        current = BACKGROUNDMUSIC;
        current.setVolume(0.20f);
        current.play();
    }

    public void playMusic(int musicID) {
        Music music = assetManager.getMusic(musicID);
        if (music == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to play music index: " + musicID + ", but it was not loaded.");
            return;
        }
        music.setVolume(volume);
        music.play();
        music.setLooping(true);
    }

    public void stopMusic(int musicID) {
        Music music = assetManager.getMusic(musicID);
        if (music == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to stop music index: " + musicID + ", but it was not loaded.");
            return;
        }
        music.stop();
    }

    public void fadeInMusic(int musicID, float fadeRate) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Music music = assetManager.getMusic(musicID);
                if (music == null) {
                    Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to stop music index: " + musicID + ", but it was not playing or loaded.");
                    return;
                }
                if (music.getVolume() < 1)
                    music.setVolume(music.getVolume() + MUSIC_FADE_STEP);
                else {
                    this.cancel();
                }
            }
        }, 0f, fadeRate);
    }

    public void fadeOutMusic(int musicID, float fadeRate) {

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Music music = assetManager.getMusic(musicID);
                if (music == null) {
                    Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to stop music index: " + musicID + ", but it was not playing or loaded.");
                    return;
                }
                if (music.getVolume() >= MUSIC_FADE_STEP)
                    music.setVolume(music.getVolume() - MUSIC_FADE_STEP);
                else {
                    music.stop();
                    this.cancel();
                }
            }
        }, 0f, fadeRate);

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

    public void volumeDown(float vol) {
        current.setVolume(current.getVolume() - vol);
    }

    public void volumeUp(float vol) {
        current.setVolume(current.getVolume() + vol);
    }

    public void toggle() {
        if (current.isPlaying()) {
            current.stop();
        } else {
            current.play();
        }
    }
}
