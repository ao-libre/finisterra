package launcher;

import object.ObjectCreator;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class CreatorLauncher {

    private static final int GAME_SCREEN_WIDTH = 600;
    private static final int GAME_SCREEN_HEIGHT = 480;
    private static final boolean GAME_FULL_SCREEN = false;
    private static final boolean GAME_VSYNC_ENABLED = true;

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Client";
        cfg.width = GAME_SCREEN_WIDTH;
        cfg.height = GAME_SCREEN_HEIGHT;
        cfg.fullscreen = GAME_FULL_SCREEN;
        cfg.vSyncEnabled = GAME_VSYNC_ENABLED;
        cfg.foregroundFPS = 0;
        cfg.resizable = false;
        new LwjglApplication(new ObjectCreator(), cfg);
    }
}
