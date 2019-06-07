import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import game.AOGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");

        // Opens Config.json to load config.
        ClientConfiguration config = ClientConfiguration.loadConfig("assets/Config.json");

        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
            cfg.setTitle("Finisterra");
            cfg.setWindowedMode(config.getWidth(),config.getHeight());
            cfg.useVsync(true);
            cfg.setIdleFPS(60);
            cfg.setResizable(true);

        new Lwjgl3Application(new AOGame(), cfg);
    }
}
