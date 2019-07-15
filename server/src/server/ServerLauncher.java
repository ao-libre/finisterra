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

        // Log in console. Un-comment this if you wish to debug Server.json's I/O
        //Log.info("[Network] Using LocalHost: " + config.getNetwork().getuseLocalHost());
        //Log.info("[Network - Ports] TCP: " + config.getNetwork().getPorts().getTcpPort());
        //Log.info("[Network - Ports] UDP: " + config.getNetwork().getPorts().getUdpPort());
        //Log.info("[Network - API] URL: " + config.getNetwork().getApi().getapiURL());
        //Log.info("[Network - API] Port: " + config.getNetwork().getApi().getApiPort());

        /**
         * Launch application
         */
        new HeadlessApplication(new Finisterra(config));
    }


}
