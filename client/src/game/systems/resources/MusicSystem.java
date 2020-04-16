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

    private final Music FIRSTBGM = Gdx.audio.newMusic(Gdx.files.internal("data/music/101.mp3"));
    private static final float MUSIC_FADE_STEP = 0.01f;
    private static float volume = 1.0f;
    private Music current = FIRSTBGM;
    private boolean disableMusic;
    @Wire
    private DefaultAOAssetManager assetManager;


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
    //todo falta implementar la forma de saber en que Screen nos encontramos y de esa forma seleccionar diferentes musicas
    protected void initialize() {
        if (!disableMusic) {
            current = FIRSTBGM;
            current.setLooping( true );
            current.play();
            fadeInMusic( 1f, 20f );
        }
    }


    public void playMusic(int musicID) {
        if (!disableMusic) {
            if(current.isPlaying()) {
                stopMusic();
            }
            current = assetManager.getMusic( musicID );
            if(current == null) {
                Log.warn( SoundsSystem.class.getSimpleName(), "Error: tried to play music index: " + musicID + ", but it was not loaded." );
                return;
            }
            current.setVolume( volume );
            current.play();
            current.setLooping( true );
        }
    }

    public void stopMusic() {
        if (current == null) {
            Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to stop music index: " + current + ", but it was not loaded.");
            return;
        }
        if (current.isLooping()){
            current.setLooping( false );
        }
        current.stop();
    }

    public void fadeInMusic(float fadeRate, float maxVol) {
        current.setVolume( 0f );
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (current == null) {
                    Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to stop music index: " + current + ", but it was not playing or loaded.");
                    return;
                }
                if (current.getVolume() < maxVol)
                    current.setVolume(current.getVolume() + MUSIC_FADE_STEP);
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
                if (current == null) {
                    Log.warn(SoundsSystem.class.getSimpleName(), "Error: tried to stop music index: " + current + ", but it was not playing or loaded.");
                    return;
                }
                if (current.getVolume() >= MUSIC_FADE_STEP)
                    current.setVolume(current.getVolume() - MUSIC_FADE_STEP);
                else {
                    current.stop();
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
    public boolean isDisableMusic(){
        return disableMusic;
    }
    public void setDisableMusic(boolean musicEnable){
        this.disableMusic = musicEnable;
    }
}
