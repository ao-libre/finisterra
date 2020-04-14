package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import server.core.Finisterra;
import shared.util.Tick;

public class ServerLauncher {

    public static void main(String[] arg) {
        // Launch application
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.renderInterval = Tick.TIME / 1000;
        new HeadlessApplication(new Finisterra());
    }
}
