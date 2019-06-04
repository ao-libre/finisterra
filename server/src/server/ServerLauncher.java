package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import server.core.Finisterra;

import server.core.ServerConfiguration;
import static server.core.ServerConfiguration.netPortType.port_TCP;
import static server.core.ServerConfiguration.netPortType.port_UDP;

public class ServerLauncher {

    public static void main(String[] arg) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

        // Opens Server.json to load config.
        ServerConfiguration serverJson = new ServerConfiguration();
        new HeadlessApplication(new Finisterra(serverJson.getServerPort(port_TCP), serverJson.getServerPort(port_UDP)));

    }

}
