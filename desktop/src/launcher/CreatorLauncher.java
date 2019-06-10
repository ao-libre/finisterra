package launcher;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import object.ObjectCreator;

public class CreatorLauncher {

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");

        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Client");
        cfg.setWindowedMode(600,480);
        cfg.useVsync(true);
        cfg.setIdleFPS(60);
        cfg.setResizable(false);

        new Lwjgl3Application(new ObjectCreator(), cfg);
    }
}