package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.esotericsoftware.minlog.Log;
import server.core.Finisterra;

public class ServerLauncher {

    public static void main(String[] arg) {

        /**
         * Load desktop Server.json or create default.
         */
        ServerConfiguration config = ServerConfiguration.loadConfig("Server.json");
        if (config == null) {
            Log.info("ServerLauncher", "Server configuration file < Server.json > not found, creating default.");
            config = ServerConfiguration.createConfig();
            config.save();
        }

        /**
         * Launch application
         */
        new HeadlessApplication(new Finisterra(config));
    }


}
