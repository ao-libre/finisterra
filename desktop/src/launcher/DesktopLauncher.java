package launcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import game.AOGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();

        Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();

        cfg.setTitle("Finisterra");
        cfg.setWindowedMode(1280,768);
        cfg.setFullscreenMode(mode);
        cfg.useVsync(true);
        cfg.setIdleFPS(0);
        cfg.setResizable(true);
        new Lwjgl3Application(new AOGame(), cfg);
    }

}
