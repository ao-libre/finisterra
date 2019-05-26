package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import game.utils.Resources;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MusicHandler {

    public static final float MUSIC_FADE_STEP = 0.01f;

    private static Map<Integer, Music> musicMap = new ConcurrentHashMap<>();
    private static Map<Integer, Sequencer> midiMap = new ConcurrentHashMap<>();

    private static String musicPath = Resources.GAME_MUSIC_PATH;
    private static String midiPath = Resources.GAME_MIDI_PATH;

    public static void load(){
        Reflections reflections = new Reflections("", new ResourcesScanner());
        Set<String> fileNames = reflections.getResources(Pattern.compile(".*\\.mp3"));
        fileNames.addAll(reflections.getResources(Pattern.compile(".*\\.mid")));
        fileNames.forEach(file -> {
            loadFile(file);
        });
    }

    private static void loadFile(String file) {
        FileHandle musicFile = Gdx.files.internal(file);
        if (musicFile.exists()) {
            switch (musicFile.extension()) {
                case Resources.GAME_MUSIC_EXTENSION:
                    loadMusic(musicFile);
                    break;
                case Resources.GAME_MIDI_EXTENSION:
                    loadMidi(musicFile);
                    break;
                default:
                    Log.info("Music extension not supported " + musicFile.extension());
                    break;
            }
        } else {
            Log.info("Trying to load music file but not found");
        }
    }

    private static void loadMidi(FileHandle file) {
        Integer midiID;

        try {
            midiID = Integer.valueOf(file.nameWithoutExtension());
        } catch (NumberFormatException e) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error:" + file.name() + " should have a numeric name.", e);
            return;
        }

        if (!midiMap.containsKey(midiID))
        {
            Sequencer sequencer = null;
            try {

                sequencer = MidiSystem.getSequencer();
                sequencer.open();
                sequencer.setSequence(file.read());
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);

            } catch (MidiUnavailableException e) {
                Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error on loadMidi(FileHandle file): Midi is not available.", e);
                return;
            } catch (InvalidMidiDataException e) {
                Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error on loadMidi(FileHandle file): Midi Data was invalid.", e);
                return;
            } catch (IOException e) {
                Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error on loadMidi(FileHandle file): IO error.", e);
                return;
            }

            midiMap.put(midiID,sequencer);
        }

    }

    private static void loadMusic(FileHandle file) {
        Integer musicID;

        try {
            musicID = Integer.valueOf(file.nameWithoutExtension());
        } catch (NumberFormatException e) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error:" + file.name() + " should have a numeric name.", e);
            return;
        }

        Music music = Gdx.audio.newMusic(file);

        if (!musicMap.containsKey(musicID))
        {
            musicMap.put(musicID,music);
        }
    }

    public static void playMusic(int musicID){

        if (!musicMap.containsKey(musicID)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play music index: " + musicID + ", but it was not loaded.");
            return;
        }
        //TODO: it should be played with a global configurable volume
        musicMap.get(musicID).play();
        musicMap.get(musicID).setLooping(true);
    }

    public static void stopMusic(int musicID){

        if (!musicMap.containsKey(musicID)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to stop music index: " + musicID + ", but it was not loaded.");
            return;
        }
        //TODO: it should be played with a global configurable volume
        musicMap.get(musicID).stop();
    }

    public static void FadeInMusic(int musicID, float fadeRate){
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!musicMap.containsKey(musicID)) {
                    Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to stop music index: " + musicID + ", but it was not playing or loaded.");
                    return;
                }
                Music music = musicMap.get(musicID);

                if (music.getVolume() < 1)
                    music.setVolume(music.getVolume()+MUSIC_FADE_STEP);
                else {
                    this.cancel();
                }
            }
        }, 0f, fadeRate);
    }

    public static void FadeOutMusic(int musicID, float fadeRate){

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!musicMap.containsKey(musicID)) {
                    Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to stop music index: " + musicID + ", but it was not playing or loaded.");
                    return;
                }
                Music music = musicMap.get(musicID);

                if (music.getVolume() >= MUSIC_FADE_STEP)
                    music.setVolume(music.getVolume()-MUSIC_FADE_STEP);
                else {
                    music.stop();
                    this.cancel();
                }
            }
        }, 0f, fadeRate);

    }

    public static void unload() {
        musicMap.forEach((k, v) -> v.dispose());
        musicMap.clear();

        midiMap.clear();
    }

    //TODO: MIDIs cant be faded at the moment
    public static void playMIDI(int midiID) {
        Sequencer sequencer = midiMap.get(midiID);

        if (sequencer == null) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play midi index: " + midiID + ", but it was not loaded.");
            return;
        }
        sequencer.start();
    }

    public static void stopMIDI(int midiID) {
        Sequencer sequencer = midiMap.get(midiID);

        if (sequencer == null) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play midi index: " + midiID + ", but it was not loaded.");
            return;
        }

        sequencer.stop();
    }
}
