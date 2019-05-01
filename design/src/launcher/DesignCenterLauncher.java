package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesignCenterLauncher {
    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Finisterra Design Center";
        cfg.width = 800;
        cfg.height = 600;
        cfg.fullscreen = false;
        cfg.vSyncEnabled = true;
        cfg.foregroundFPS = 0;
        cfg.resizable = true;
        new LwjglApplication(new DesignCenter(), cfg);
    }
}
