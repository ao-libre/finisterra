package launcher;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.ClientConfiguration;
import game.ClientConfiguration.Init;
import game.ClientConfiguration.Init.Video;

import static game.utils.Resources.CLIENT_CONFIG;

public class DesktopLauncher {

    public static final String CLIENT_CONFIG_JSON = CLIENT_CONFIG;

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");

        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();

        ClientConfiguration config = ClientConfiguration.loadConfig(CLIENT_CONFIG_JSON);
        cfg.setTitle("Finisterra - Argentum Online Java");
        Init initConfig = config.getInitConfig();
        Video video = initConfig.getVideo();
        cfg.setWindowedMode(video.getWidth(), video.getHeight());
        cfg.useVsync(video.getVsync());
        cfg.setIdleFPS(60);
        cfg.setResizable(initConfig.isResizeable());
        cfg.disableAudio(initConfig.isDisableAudio());
        cfg.setMaximized(initConfig.isStartMaximized());

        // TODO use enum instead of strings
        if (video.getHiDPIMode() == "Pixels") {
            cfg.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Pixels);
        } else {
            cfg.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Logical);
        }

        // Log in console.
        Log.info("Initializing launcher...");
//        Log.info("[Parameters - Window] Width: " + config.getClientWidth());
//        Log.info("[Parameters - Window] Height: " + config.client_Height());
//        Log.info("[Parameters - Window] Start Maximized: " + config.client_startMaximized());
//        Log.info("[Parameters - Window] Resizeable: " + config.client_Resizeable());
//        Log.info("[Parameters - Graphics] vSync: " + config.client_VSync());
//        Log.info("[Parameters - Graphics] HiDPI Mode: " + config.client_HiDPI_Mode());
//        Log.info("[Parameters - Audio] Disabled: " + config.client_noAudio());

        new Lwjgl3Application(new AOGame(), cfg);
    }
}
