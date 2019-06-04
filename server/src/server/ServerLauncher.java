package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import server.core.Finisterra;

import server.core.ServerConfiguration;

public class ServerLauncher {

    public static void main(String[] arg) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

        ServerConfiguration.createDefaultJson();

        // Opens Server.json to load config.
        ServerConfiguration.loadConfig();
        new HeadlessApplication(new Finisterra(ServerConfiguration.getTcpPort(), ServerConfiguration.getUdpPort()));

    }

}
