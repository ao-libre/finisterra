package game.handlers;

import com.badlogic.gdx.audio.Sound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SoundsHandler {

    private static Map<Integer, Sound> soundsMap = new ConcurrentHashMap<>();

    public static void load(){
        Sound newSound = new Sound();
        
    }
}
