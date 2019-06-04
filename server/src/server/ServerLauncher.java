package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import server.core.Finisterra;

import server.core.ServerConfiguration;

public class ServerLauncher {

    private static final String CONFIG_FILE = "resources/server.json";

    public static void main(String[] arg) {
        ServerConfiguration.createDefaultJson();

        // Opens Server.json to load config.
        ServerConfiguration config = ServerConfiguration.loadConfig(CONFIG_FILE);
        new HeadlessApplication(new Finisterra());

    }

}
