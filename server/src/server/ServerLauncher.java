package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import server.core.Finisterra;

public class ServerLauncher {

    public static void main(String[] arg) {
        new HeadlessApplication(new Finisterra(7666, 7667));
    }
}
