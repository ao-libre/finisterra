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

public class ClientConfiguration {
    private Init initConfig;
    private Network network;

    public void setInitConfig(Init initConfig) {
        this.initConfig = initConfig;
    }

    public void setNetwork(Network net) {
        this.network = network;
    }

    public Init getInitConfig() {
        return initConfig;
    }

    public Network getNetwork() {
        return network;
    }

    public static ClientConfiguration loadConfig(String path) {
        Json configObject = new Json();

        configObject.setOutputType(JsonWriter.OutputType.json);// esto hace que cuando escribas con el toJson lo guarde en formato json)
        configObject.setIgnoreUnknownFields(true); // hace que si no conoce un campo, lo ignore

        try {
            InputStream configFile = new FileInputStream("assets/Config.json");

            return configObject.fromJson(ClientConfiguration.class, configFile);

        } catch (FileNotFoundException ex) {
            Log.debug("Client configuration file not found!");
        }

        return null;
    }

    public static void createConfig() {

            Json configObject = new Json();
            ClientConfiguration configOutput = new ClientConfiguration();

            // Default values of `Init`
            configOutput.setInitConfig(new Init());

                // Default values of `Init.Video`
                Init.Video video = new Init.Video();
                    video.setWidth(1280);
                    video.setHeight(720);
                    configOutput.getInitConfig().setVideo(video);

            // Default values of `Network`
            configOutput.setNetwork(new Network());

                // Default values of `Network.defaultServer`
                Network.DefaultServer defServer = new Network.DefaultServer();
                    defServer.setHostname("45.235.98.116");
                    defServer.setPort(9000);
                configOutput.getNetwork().setDefaultServer(defServer);

            FileHandle outputFile = Gdx.files.local("output/Config.json");

            configObject.toJson(configObject,outputFile);
    }

    // ---------------------------------------------------------------
    // Aca asignas los valores de las propiedades en el Server.json
    // ---------------------------------------------------------------

    //----------------------------------------------------------------------------
    private static class Init {
        private Video video;

        /*
           "Init": {
                //Estás acá.
            }
        */

        private void setVideo(Video video) {
            this.video = video;
        }

        private static class Video {
            private int width;
            private int height;

            public void setHeight(int height) {
                this.height = height;
            }

            public void setWidth(int width) {
                this.width = width;
            }

        }
    }

    //-------------------------------------------------------------------------
    private static class Network {
        private DefaultServer defaultServer;

        /*
           "network": {
                //Estás acá.
            }
        */

        private void setDefaultServer(DefaultServer defaultServer) {
            this.defaultServer = defaultServer;
        }

        public DefaultServer getDefaultServer() {
            return defaultServer;
        }

        private static class DefaultServer {
            private String hostname;
            private int port;

            /*
                "network": {
                    "DefaultServer": {
                        // Estás acá.
                        "hostname": hostname,
                        "port": port

                    }
                }
             */

            private void setHostname(String hostname) {
                this.hostname = hostname;
            }

            private void setPort(int port) {
                this.port = port;
            }
        }
    }

}
