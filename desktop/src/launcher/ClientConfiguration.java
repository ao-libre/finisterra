package launcher;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.esotericsoftware.minlog.Log;

import java.io.InputStream;
import java.io.FileInputStream;
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
            private final int width;
            private final int height;

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
            private final String hostname;
            private final int port;

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
