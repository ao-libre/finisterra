package launcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.files.FileHandleStream;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class ClientConfiguration {

    private Init initConfig;
    private Network net;

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
            configOutput.initConfig.video.width = 1280;
            configOutput.initConfig.video.height = 720;

            // Default values of `Init`
            configOutput.net.defaultServer.hostname = "45.235.98.116";
            configOutput.net.defaultServer.port = 9000;

            FileHandle outputFile = Gdx.files.local("output/Config.json");

            configObject.toJson(configObject,outputFile));
    }

    // ---------------------------------------------------------------
    // Aca obtenes los valores de las propiedades en el Server.json
    // ---------------------------------------------------------------

    // Init
    public int getWidth() {
        return initConfig.video.width;
    }

    public int getHeight() {
        return initConfig.video.height;
    }

    // Network
    public String getHostname() {
        return net.defaultServer.hostname;
    }

    public int getPort() {
        return net.defaultServer.port;
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

        public Init(Video video) {
            this.video = video;
        }

        private static class Video {
            private int width;
            private int height;

            public Video(int width, int height) {
                this.width = width;
                this.height = height;
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

        public Network(DefaultServer defaultServer) {
            this.defaultServer = defaultServer;
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

            public DefaultServer(String hostname, int port) {
                this.hostname = hostname;
                this.port = port;
            }
        }
    }

}
