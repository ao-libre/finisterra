package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import game.utils.Resources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SoundsHandler {

    private static Map<Integer, Sound> soundsMap = new ConcurrentHashMap<>();

    private static String soundsPath = Resources.GAME_SOUNDS_PATH;

    public static void load(){
        FileHandle file = Gdx.app.getFiles().internal(soundsPath);

        if (!file.isDirectory())
            return;

        for (FileHandle tmp : file.list()) {
            if (tmp.extension().equals(Resources.GAME_SOUNDS_EXTENSION)){
                Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Cargando " + tmp.name());
                loadSound(tmp);
            }else {
                String tmpExt = tmp.extension();
                Gdx.app.debug(SoundsHandler.class.getSimpleName(), tmpExt);
            }
        }

    }

    public static void unload() {
        soundsMap.forEach((k,v) -> v.dispose());
        soundsMap.clear();
    }

    private static void loadSound(FileHandle file){
        Integer soundID;

        try {
            soundID = Integer.valueOf(file.nameWithoutExtension());
        } catch (NumberFormatException e) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error:" + file.name() + " should have a numeric name.", e);
            return;
        }

        Sound sound = Gdx.audio.newSound(file);

        if (!soundsMap.containsKey(soundID))
        {
            soundsMap.put(soundID,sound);
        }
    }

    public static long playSound(Integer soundID, boolean loop) {
        if (!soundsMap.containsKey(soundID)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + soundID + ", but it was not loaded.");
            return -1;
        }
        //TODO: it should be played with a global configurable volume
        if (loop == false){
            return soundsMap.get(soundID).play();
        }
        else
        {
            return soundsMap.get(soundID).loop();
        }
    }

    public static void playSound(Integer soundID) {
        playSound(soundID, false);
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
