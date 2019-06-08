package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class ServerConfiguration {
    private Network network;

    // Objeto 'Init'
    public void setNetwork(Network network) { this.network = network; }

    // Objeto 'Network'
    public Network getNetwork() {
        return network;
    }

    // ¡USE THIS GETTERS TO RETRIEVE THE CONFIG.JSON VALUES!
    public int portTCP() { return getNetwork().getPorts().getTcp(); }
    public int portUDP() { return getNetwork().getPorts().getUdp(); }
    public boolean useLocalhost() { return getNetwork().getUseLocalhost(); }

    public static ServerConfiguration loadConfig(String path) {
        Json configObject = new Json();

        configObject.setOutputType(JsonWriter.OutputType.json);// esto hace que cuando escribas con el toJson lo guarde en formato json)
        configObject.setIgnoreUnknownFields(true); // hace que si no conoce un campo, lo ignore

        try {
            // DO NOT USE 'Gdx.Files' , because 'Gdx.Files' in the launcher is always NULL!
            InputStream configFile = new FileInputStream(path);

            return configObject.fromJson(ServerConfiguration.class, configFile);

        } catch (FileNotFoundException ex) {
            Log.debug("Server configuration file not found!");
        }

        return null;
    }

    public static void createConfig() {

        Json configObject = new Json();

        configObject.setOutputType(JsonWriter.OutputType.json);// esto hace que cuando escribas con el toJson lo guarde en formato json)
        configObject.setIgnoreUnknownFields(true); // hace que si no conoce un campo, lo ignore

        // WARNING: Set ALL BOOLEAN parameters to TRUE in this method, else, it won't write (the FALSE value in Config.json).
        ServerConfiguration configOutput = new ServerConfiguration();

        // Default values of `Network`
        configOutput.setNetwork(new Network());

            configOutput.getNetwork().setUseLocalhost(true);

            Network.Ports netConfig = new Network.Ports();
                netConfig.setTcp(9000);
                netConfig.setUdp(9001);
            configOutput.getNetwork().setPorts(netConfig);

        FileHandle outputFile = Gdx.files.local("output/Server.json");

        configObject.toJson(configOutput,outputFile);
    }

    // ---------------------------------------------------------------
    // Aca asignas los valores de las propiedades en el Server.json
    // ---------------------------------------------------------------

    //-------------------------------------------------------------------------
    private static class Network {
        private boolean useLocalhost;
        private Ports ports;

        /*
           "network": {
                //Estás acá.
                "useLocalhost": useLocalhost
            }
        */

        private void setUseLocalhost(boolean useLocalhost) {
            this.useLocalhost = useLocalhost;
        }
        private void setPorts(Ports ports) { this.ports = ports; }

        private Ports getPorts() { return ports; }
        public boolean getUseLocalhost() { return useLocalhost; }

        private static class Ports {
            private int tcp;
            private int udp;

            /*
                "network": {
                    "useLocalhost": useLocalhost,
                    "ports": {
                        // Estás acá.
                        "tcp": tcp,
                        "udp": udp

                    }
                }
             */

           private void setTcp(int tcp) { this.tcp = tcp; }
           private void setUdp(int udp) { this.udp = udp; }

           private int getTcp() { return tcp; }
           private int getUdp() { return udp;  }
        }
    }

}