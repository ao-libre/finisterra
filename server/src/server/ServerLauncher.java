package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import server.core.Finisterra;

public class ServerLauncher {

    public static void main(String[] arg) {
        // Launch application
        new HeadlessApplication(new Finisterra());
    }
}
