package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.minlog.Log;
import game.systems.sound.SoundSytem;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


public class SoundsHandler {

    private static Map<Integer, Sound> soundsMap = new ConcurrentHashMap<>();

    public static void load() {
        Reflections reflections = new Reflections("", new ResourcesScanner());
        Set<String> fileNames = reflections.getResources(Pattern.compile(".*\\.wav"));
        fileNames.forEach(file -> {
            loadFile(file);
        });
    }

    private static void loadFile(String file) {
        FileHandle musicFile = Gdx.files.internal(file);
        if (musicFile.exists()) {
            loadSound(musicFile);
        } else {
            Log.info("Sound file not found " + musicFile.name());
        }
    }

    public static void unload() {
        soundsMap.forEach((k, v) -> v.dispose());
        soundsMap.clear();
    }

    private static void loadSound(FileHandle file) {
        Integer soundID;

        try {
            soundID = Integer.valueOf(file.nameWithoutExtension());
        } catch (NumberFormatException e) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error:" + file.name() + " should have a numeric name.", e);
            return;
        }

        Sound sound = Gdx.audio.newSound(file);

        if (!soundsMap.containsKey(soundID)) {
            soundsMap.put(soundID, sound);
        }
    }

    public static long playSound(Integer soundID, boolean loop) {
        if (!soundsMap.containsKey(soundID)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return -1;
        }
        //TODO: it should be played with a global configurable volume
        if (loop == false) {
            return soundsMap.get(soundID).play(SoundSytem.volume);
        } else {
            return soundsMap.get(soundID).loop(SoundSytem.volume);
        }
    }

    public static void playSound(Integer soundID) {
        playSound(soundID, false);
    }

    public static void updateVolume(Integer soundId, long soundIndex, float volume) {
        if (soundsMap.containsKey(soundId)) {
            soundsMap.get(soundId).setVolume(soundIndex, volume * SoundSytem.volume);
        }
    }

    public static void updatePan(Integer soundId, long soundIndex, float pan, float volume) {
        if (soundsMap.containsKey(soundId)) {
            soundsMap.get(soundId).setPan(soundIndex, pan, volume * SoundSytem.volume);
        }
    }

    public static void stopSound(Integer soundID) {
        if (!soundsMap.containsKey(soundID)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return;
        }

        soundsMap.get(soundID).stop();
    }

    public static void stopSound(Integer soundID, long soundIndex) {
        if (!soundsMap.containsKey(soundID)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return;
        }

        soundsMap.get(soundID).stop(soundIndex);
    }

}
