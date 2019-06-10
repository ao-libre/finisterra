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
    private static ClientConfiguration loadedConfiguration;
    private Init initConfig;
    private Network network;

    // Objeto 'Init'
    private void setInitConfig(Init initConfig) {
        this.initConfig = initConfig;
    }
    public void setNetwork(Network network) { this.network = network; }

    // Objeto 'Network'
    private Init getInitConfig() { return initConfig; }
    public Network getNetwork() {
        return network;
    }

    // ¡USE THIS GETTERS TO RETRIEVE THE CONFIG.JSON VALUES!
    public int client_Width() { return getInitConfig().getVideo().getWidth(); }
    public int client_Height() { return getInitConfig().getVideo().getHeight(); }
    public boolean client_VSync() { return getInitConfig().getVideo().getVsync(); }
    public boolean client_Resizeable() { return getInitConfig().getResizeable();}
    public boolean client_noAudio() { return getInitConfig().getDisableAudio(); }
    public boolean client_startMaximized() { return getInitConfig().getStartMaximized(); }
    public String client_HiDPI_Mode() { return getInitConfig().getVideo().getHiDPI_Mode(); }

    public static ClientConfiguration loadConfig(String path) {
        Json configObject = new Json();

        configObject.setOutputType(JsonWriter.OutputType.json);// esto hace que cuando escribas con el toJson lo guarde en formato json)
        configObject.setIgnoreUnknownFields(true); // hace que si no conoce un campo, lo ignore

        try {
            // DO NOT USE 'Gdx.Files' , because 'Gdx.Files' in the launcher is always NULL!
            InputStream configFile = new FileInputStream(path);

            loadedConfiguration = configObject.fromJson(ClientConfiguration.class, configFile);

            return loadedConfiguration;

        } catch (FileNotFoundException ex) {
            Log.debug("Client configuration file not found!");
        }

        return null;
    }

    public static String client_getDefaultHost() {
        return loadedConfiguration.getNetwork().getDefaultServer().getHostname();
    }

    public static int client_getDefaultPort() {
        return loadedConfiguration.getNetwork().getDefaultServer().getPort();
    }

    public static void createConfig() {

            Json configObject = new Json();

            configObject.setOutputType(JsonWriter.OutputType.json);// esto hace que cuando escribas con el toJson lo guarde en formato json)
            configObject.setIgnoreUnknownFields(true); // hace que si no conoce un campo, lo ignore

            // WARNING: Set ALL BOOLEAN parameters to TRUE in this method, else, it won't write (the FALSE value in Config.json).
            ClientConfiguration configOutput = new ClientConfiguration();

            // Default values of `Init`
            configOutput.setInitConfig(new Init());
                configOutput.getInitConfig().setResizeable(true);
                configOutput.getInitConfig().setDisableAudio(true);
                configOutput.getInitConfig().setStartMaximized(true);

                // Default values of `Init.Video`
                Init.Video video = new Init.Video();
                    video.setWidth(1280);
                    video.setHeight(720);
                    video.setVsync(true);
                    video.setHiDPI_Mode("Logical");
                    configOutput.getInitConfig().setVideo(video);

            // Default values of `Network`
            configOutput.setNetwork(new Network());

                // Default values of `Network.defaultServer`
                Network.DefaultServer defServer = new Network.DefaultServer();
                    defServer.setHostname("45.235.98.116");
                    defServer.setPort(9000);
                configOutput.getNetwork().setDefaultServer(defServer);

            FileHandle outputFile = Gdx.files.local("output/Config.json");

            configObject.toJson(configOutput,outputFile);
    }

    // ---------------------------------------------------------------
    // Aca asignas los valores de las propiedades en el Server.json
    // ---------------------------------------------------------------

    //----------------------------------------------------------------------------
    private static class Init {
        private Video video;
        private boolean resizeable;
        private boolean disableAudio;
        private boolean startMaximized;

        /*
           "Init": {
                //Estás acá.
            }
        */

        private void setVideo(Video video) {
            this.video = video;
        }
        private void setResizeable(boolean resizeable) { this.resizeable = resizeable; }
        private void setDisableAudio(boolean disableAudio) { this.disableAudio = disableAudio; }
        private void setStartMaximized(boolean startMaximized) { this.startMaximized = startMaximized; }

        private Video getVideo() { return video; }
        private boolean getResizeable() { return resizeable; }
        private boolean getDisableAudio() { return disableAudio; }
        private boolean getStartMaximized() { return startMaximized; }

        private static class Video {
            private int width;
            private int height;
            private boolean vSync;
            private String HiDPI_Mode;

            public void setHeight(int height) {
                this.height = height;
            }
            public void setWidth(int width) {
                this.width = width;
            }
            public void setVsync(boolean vSync) { this.vSync = vSync; }
            public void setHiDPI_Mode(String HiDPI_Mode) { this.HiDPI_Mode = HiDPI_Mode; }

            private int getWidth() { return width; }
            private int getHeight() { return height; }
            private boolean getVsync() { return vSync; }
            private String getHiDPI_Mode() { return HiDPI_Mode; }
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

            public String getHostname() { return hostname; }
            public int getPort() { return port; }
        }
    }

}
