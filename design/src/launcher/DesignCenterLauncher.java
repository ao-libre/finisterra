package launcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesignCenterLauncher {
    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();

        Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();

        cfg.setTitle("Finisterra Design Center");
        cfg.setWindowedMode(800,600);
        cfg.setFullscreenMode(mode);
        cfg.useVsync(true);
        cfg.setIdleFPS(0);
        cfg.setResizable(true);
        new Lwjgl3Application(new DesignCenter(), cfg);
    }
}
