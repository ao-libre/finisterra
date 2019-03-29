package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import server.core.Server;

public class ServerLauncher {

    public static void main (String[] arg) {
        new HeadlessApplication(new Server());
    }
}
