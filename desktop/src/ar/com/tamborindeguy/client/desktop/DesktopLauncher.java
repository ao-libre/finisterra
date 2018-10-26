package ar.com.tamborindeguy.client.desktop;

import ar.com.tamborindeguy.client.game.AO;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "true");
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Client";
        cfg.width = AO.GAME_SCREEN_WIDTH;
        cfg.height = AO.GAME_SCREEN_HEIGHT;
        cfg.fullscreen = AO.GAME_FULL_SCREEN;
        cfg.vSyncEnabled = AO.GAME_VSYNC_ENABLED;
        cfg.foregroundFPS = 0;
        cfg.resizable = false;
        new LwjglApplication(new AO(), cfg);
	}
}
