package game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

/**
 * @todo distinguish between Desktop-specific configuration (LWJGL configuration) and platform-independent configuration (Client, Network, etc.).
 * @see {@link AOGame}
 */
public class ClientConfiguration {

    private Init initConfig;
    private Network network;

    public static ClientConfiguration loadConfig(String path) {
        Json configObject = new AOJson();
        try {
            // Before GDX initialization
            // DO NOT USE 'Gdx.Files', because 'Gdx.Files' in the launcher is always NULL!
            return configObject.fromJson(ClientConfiguration.class, new FileHandle(path));

        } catch (Exception ex) {
            Log.debug("Client configuration file not found!");
        }

        return null;
    }

    // useful for client json sample creation
    public static ClientConfiguration createConfig() {
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

        return configOutput;
    }

    public Init getInitConfig() {
        return initConfig;
    }

    private void setInitConfig(Init initConfig) {
        this.initConfig = initConfig;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void save() {
        Json json = new AOJson();
        json.toJson(this, new FileHandle("assets/config.json"));
    }

    public static class Init {
        private Video video;
        private boolean resizeable;
        private boolean disableAudio;
        private boolean startMaximized;

        public Video getVideo() {
            return video;
        }

        void setVideo(Video video) {
            this.video = video;
        }

        public boolean isDisableAudio() {
            return disableAudio;
        }

        void setDisableAudio(boolean disableAudio) {
            this.disableAudio = disableAudio;
        }

        public boolean isResizeable() {
            return resizeable;
        }

        void setResizeable(boolean resizeable) {
            this.resizeable = resizeable;
        }

        public boolean isStartMaximized() {
            return startMaximized;
        }

        void setStartMaximized(boolean startMaximized) {
            this.startMaximized = startMaximized;
        }

        public static class Video {
            private int width;
            private int height;
            private boolean vSync;
            private String HiDPI_Mode;

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public boolean getVsync() {
                return vSync;
            }

            private void setVsync(boolean vSync) {
                this.vSync = vSync;
            }

            public String getHiDPIMode() {
                return HiDPI_Mode;
            }

            private void setHiDPIMode(String HiDPI_Mode) {
                this.HiDPI_Mode = HiDPI_Mode;
            }
        }
    }

    public static class Network {
        private DefaultServer defaultServer;

        public DefaultServer getDefaultServer() {
            return defaultServer;
        }

        void setDefaultServer(DefaultServer defaultServer) {
            this.defaultServer = defaultServer;
        }

        public static class DefaultServer {
            private String hostname;
            private int port;

            public String getHostname() {
                return hostname;
            }

            void setHostname(String hostname) {
                this.hostname = hostname;
            }

            public int getPort() {
                return port;
            }

            void setPort(int port) {
                this.port = port;
            }
        }
    }
}
