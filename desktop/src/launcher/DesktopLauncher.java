package launcher;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.ClientConfiguration;
import game.ClientConfiguration.Init;
import game.ClientConfiguration.Init.Video;

import static game.utils.Resources.CLIENT_CONFIG;

public class DesktopLauncher {

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");

        /**
         * Load desktop config.json or create default.
         */
        ClientConfiguration config = ClientConfiguration.loadConfig(CLIENT_CONFIG);
        if (config == null) {
            Log.info("DesktopLauncher", "Desktop config.json not found, creating default.");
            config = ClientConfiguration.createConfig();
            config.save(CLIENT_CONFIG);
        }
        Init initConfig = config.getInitConfig();
        Video video = initConfig.getVideo();

        /**
         * Build LWJGL configuration
         */
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Finisterra - Argentum Online Java");
        cfg.setWindowedMode(video.getWidth(), video.getHeight());
        cfg.useVsync(video.getVsync());
        cfg.setIdleFPS(72);
        cfg.setResizable(initConfig.isResizeable());
        cfg.disableAudio(initConfig.isDisableAudio());
        cfg.setMaximized(initConfig.isStartMaximized());

        // TODO use enum instead of strings
        if (video.getHiDPIMode().equalsIgnoreCase("Pixels")) {
            cfg.setHdpiMode(HdpiMode.Pixels);
        } else {
            cfg.setHdpiMode(HdpiMode.Logical);
        }

        // Log in console. Un-comment the rest if you wish to debug Config.json's I/O
        Log.info("Initializing launcher...");
        //Log.info("[Parameters - Window] Width: " + video.getWidth());
        //Log.info("[Parameters - Window] Height: " + video.getHeight());
        //Log.info("[Parameters - Window] Start Maximized: " + initConfig.isStartMaximized());
        //Log.info("[Parameters - Window] Resizeable: " + initConfig.isResizeable());
        //Log.info("[Parameters - Graphics] vSync: " + video.getVsync());
        //Log.info("[Parameters - Graphics] HiDPI Mode: " + video.getHiDPIMode());
        //Log.info("[Parameters - Audio] Disabled: " + initConfig.isDisableAudio());

        /**
         * Launch application
         */
        new Lwjgl3Application(new AOGame(), cfg);
    }
}
