package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import game.AOGame;

public class DesktopLauncher {

    // TODO: Read from config file?
    public static final int GAME_SCREEN_WIDTH = 1280;
    public static final int GAME_SCREEN_HEIGHT = 768;
    public static final boolean GAME_FULL_SCREEN = false;
    public static final boolean GAME_VSYNC_ENABLED = true;

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Finisterra";
        cfg.width = GAME_SCREEN_WIDTH;
        cfg.height = GAME_SCREEN_HEIGHT;
        cfg.fullscreen = GAME_FULL_SCREEN;
        cfg.vSyncEnabled = GAME_VSYNC_ENABLED;
        cfg.foregroundFPS = 0;
        cfg.resizable = true;
        new LwjglApplication(new AOGame(), cfg);
    }
}
