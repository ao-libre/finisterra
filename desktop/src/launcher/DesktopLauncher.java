package launcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.ClientConfiguration;


public class DesktopLauncher {

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");

        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();

        ClientConfiguration config = ClientConfiguration.loadConfig("assets/Config.json");
            cfg.setTitle("Finisterra - Argentum Online Java");
            cfg.setWindowedMode(config.client_Width(),config.client_Height());
            cfg.useVsync(config.client_VSync());
            cfg.setIdleFPS(60);
            cfg.setResizable(config.client_Resizeable());
            cfg.disableAudio(config.client_noAudio());
            cfg.setMaximized(config.client_startMaximized());

            if (config.client_HiDPI_Mode() == "Pixels") {
                cfg.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Pixels);
            } else {
                cfg.setHdpiMode(Lwjgl3ApplicationConfiguration.HdpiMode.Logical);
            }

            // Log in console.
            Log.info("Initializing launcher...");
            Log.info("[Parameters - Window] Width: " + config.client_Width());
            Log.info("[Parameters - Window] Height: " + config.client_Height());
            Log.info("[Parameters - Window] Start Maximized: " + config.client_startMaximized());
            Log.info("[Parameters - Window] Resizeable: " + config.client_Resizeable());
            Log.info("[Parameters - Graphics] vSync: " + config.client_VSync());
            Log.info("[Parameters - Graphics] HiDPI Mode: " + config.client_HiDPI_Mode());
            Log.info("[Parameters - Audio] Disabled: " + config.client_noAudio());

        new Lwjgl3Application(new AOGame(), cfg);
    }
}