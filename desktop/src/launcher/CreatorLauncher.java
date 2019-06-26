package launcher;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.esotericsoftware.minlog.Log;
import game.ClientConfiguration;
import object.ObjectCreator;

import static game.utils.Resources.CLIENT_CONFIG;

public class CreatorLauncher {

    public static final String CLIENT_CONFIG_JSON = CLIENT_CONFIG;

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");

        ClientConfiguration config = ClientConfiguration.loadConfig(CLIENT_CONFIG_JSON);
        if (config == null) {
            Log.info("DesktopLauncher", "Desktop config.json not found, creating default.");
            config = ClientConfiguration.createConfig();
            config.save();
        }
        ClientConfiguration.Init initConfig = config.getInitConfig();
        ClientConfiguration.Init.Video video = initConfig.getVideo();

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
        if (video.getHiDPIMode() == "Pixels") {
            cfg.setHdpiMode(HdpiMode.Pixels);
        } else {
            cfg.setHdpiMode(HdpiMode.Logical);
        }

        new Lwjgl3Application(new ObjectCreator(), cfg);
    }
}
