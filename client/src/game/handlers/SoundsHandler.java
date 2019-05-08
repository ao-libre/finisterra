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
        Integer soundIdx;

        try {
            soundIdx = Integer.valueOf(file.nameWithoutExtension());
        } catch (NumberFormatException e) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error:" + file.name() + " should have a numeric name.", e);
            return;
        }

        Sound sound = Gdx.audio.newSound(file);

        if (!soundsMap.containsKey(soundIdx))
        {
            soundsMap.put(soundIdx,sound);
        }
    }

    public static void playSound(Integer index) {
        if (!soundsMap.containsKey(index)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + index + ", but it was not loaded.");
            return;
        }
        //TODO: it should be played with a global configurable volume
        soundsMap.get(index).play();
    }

    public static void loopSound(Integer index) {
        if (!soundsMap.containsKey(index)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + index + ", but it was not loaded.");
            return;
        }
        //TODO: it should be played with a global configurable volume
        soundsMap.get(index).loop();
    }

    public static void stopLoopSound(Integer index) {
        if (!soundsMap.containsKey(index)) {
            Gdx.app.debug(SoundsHandler.class.getSimpleName(), "Error: tried to play sound index: " + index + ", but it was not loaded.");
            return;
        }
        //TODO: it should be played with a global configurable volume
        soundsMap.get(index).stop();
    }

}
