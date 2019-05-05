package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import server.core.Finisterra;

public class ServerLauncher {

    public static void main(String[] arg) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new Finisterra(7666, 7667));

    }
}
