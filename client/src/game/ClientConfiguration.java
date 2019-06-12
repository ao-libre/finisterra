package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClientConfiguration {

    private Init initConfig;
    private Network network;

    private void setInitConfig(Init initConfig) {
        this.initConfig = initConfig;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public Init getInitConfig() {
        return initConfig;
    }

    public Network getNetwork() {
        return network;
    }

    public static ClientConfiguration loadConfig(String path) {
        Json configObject = new AOJson();
        try {
            // Before GDX initialization
            // DO NOT USE 'Gdx.Files', because 'Gdx.Files' in the launcher is always NULL!
            InputStream configFile = new FileInputStream(path);
            return configObject.fromJson(ClientConfiguration.class, configFile);

        } catch (FileNotFoundException ex) {
            Log.debug("Client configuration file not found!");
        }

        return null;
    }

    // useful for client json sample creation
    public static void createConfig() {

        Json configObject = new AOJson();
        // Default values will not be written down
        ClientConfiguration configOutput = new ClientConfiguration();

        // Default values of `Init`
        configOutput.setInitConfig(new Init());
        Init initConfig = configOutput.getInitConfig();
        initConfig.setResizeable(true);
        initConfig.setDisableAudio(true);
        initConfig.setStartMaximized(true);

        // Default values of `Init.Video`
        Init.Video video = new Init.Video();
        video.setWidth(1280);
        video.setHeight(720);
        video.setVsync(true);
        video.setHiDPIMode("Logical");
        configOutput.getInitConfig().setVideo(video);

        // Default values of `Network`
        configOutput.setNetwork(new Network());

        // Default values of `Network.defaultServer`
        Network.DefaultServer defServer = new Network.DefaultServer();
        defServer.setHostname("45.235.98.116");
        defServer.setPort(9000);
        configOutput.getNetwork().setDefaultServer(defServer);

        FileHandle outputFile = Gdx.files.local("output/Config.json");

        configObject.toJson(configOutput, outputFile);
    }
    
    public final static class Init {
        private Video video;
        private boolean resizeable;
        private boolean disableAudio;
        private boolean startMaximized;

        void setVideo(Video video) {
            this.video = video;
        }

        void setResizeable(boolean resizeable) {
            this.resizeable = resizeable;
        }

        void setDisableAudio(boolean disableAudio) {
            this.disableAudio = disableAudio;
        }

        void setStartMaximized(boolean startMaximized) {
            this.startMaximized = startMaximized;
        }

        public Video getVideo() {
            return video;
        }

        public boolean isDisableAudio() {
            return disableAudio;
        }

        public boolean isResizeable() {
            return resizeable;
        }

        public boolean isStartMaximized() {
            return startMaximized;
        }

        public final static class Video {
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

            private void setVsync(boolean vSync) {
                this.vSync = vSync;
            }

            private void setHiDPIMode(String HiDPI_Mode) {
                this.HiDPI_Mode = HiDPI_Mode;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public boolean getVsync() {
                return vSync;
            }

            public String getHiDPIMode() {
                return HiDPI_Mode;
            }
        }
    }

    public final static class Network {
        private DefaultServer defaultServer;

        void setDefaultServer(DefaultServer defaultServer) {
            this.defaultServer = defaultServer;
        }

        public DefaultServer getDefaultServer() {
            return defaultServer;
        }

        public final static class DefaultServer {
            private String hostname;
            private int port;

            void setHostname(String hostname) {
                this.hostname = hostname;
            }

            void setPort(int port) {
                this.port = port;
            }

            public String getHostname() {
                return hostname;
            }

            public int getPort() {
                return port;
            }
        }
    }

}
