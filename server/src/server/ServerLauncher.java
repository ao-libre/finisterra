package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import server.core.Finisterra;

import server.core.ServerConfiguration;

public class ServerLauncher {

    public static void main(String[] arg) {
        ServerConfiguration config = new ServerConfiguration();

        ServerConfiguration.createDefaultJson();

        // Opens Server.json to load config.
        ServerConfiguration.loadConfig("resources/server.json");
        new HeadlessApplication(new Finisterra(config));

    }

}
