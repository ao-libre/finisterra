package game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Este es el POJO del archivo de configuracion del cliente [Config.json]
 *
 * @see AOGame
 */
public class Config {

    public Init initConfig;
    public Account account;
    public Network network;

    /**
     * Verifica que exista el archivo de configuración
     * @param path Ruta del archivo
     * @return true si existe
     */
    public static boolean fileExists(String path) {
        try (FileInputStream is = new FileInputStream(path)) {}
        catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Carga el archivo JSON y lo parsea, genera el POJO Config.
     * @param path La ruta donde se encuentra el config.json a cargar.
     * @throws IOException Si por alguna misteriosa razon no se puede leer el archivo.
     */
    public static Config fileLoad(String path) throws RuntimeException {
        Json jsonParser = new AOJson();
        try (FileInputStream is = new FileInputStream(path)) {
            return jsonParser.fromJson(Config.class, is);
        } catch (IOException ex) {
            Log.error("Unexpected error reading config file: " + path);
            throw new RuntimeException();
        }
    }

    /**
     * Este metodo guarda el archivo json.
     * @param path La ruta donde queres que se guarde el json generado.
     */
    public void fileSave(String path) {
        Json jsonParser = new AOJson();
        jsonParser.toJson(this, new FileHandle(path));
    }

    /**
     * Crea un objeto con los valores de configuracion por defecto por si el archivo no existe.
     * @return Una instancia de la clase Config, con los valores por defecto.
     */
    public static Config getDefault() {
        // Default values will not be written down
        Config config = new Config();

        // Default values of `Init`
        config.setInitConfig(new Init());
        Init initConfig = config.getInitConfig();
        initConfig.setLanguage("es_AR");
        initConfig.setResizeable(true);
        initConfig.setDisableAudio(false);
        initConfig.setStartMaximized(true);

        // Default values of `Init.Video`
        Init.Video video = new Init.Video();
        video.setWidth(1280);
        video.setHeight(720);
        video.setVsync(true);
        video.setHiDPIMode("Logical");
        config.getInitConfig().setVideo(video);

        // Default values of `Account`
        Account account = new Account();
        account.email = "";
        account.password = "";
        config.account = account;

        // Default values of `Network`
        config.network = new Network();

        // Default values of `Network.servers`
        Array<Network.Server> servers = config.network.servers;
        servers.add(new Network.Server("localhost", "127.0.0.1", 8667));
        servers.add(new Network.Server("Servidor @recox", "45.235.98.29", 8667));

        return config;
    }

    public Init getInitConfig() {
        return initConfig;
    }

    private void setInitConfig(Init initConfig) {
        this.initConfig = initConfig;
    }

    /**
     * De aca en adelante esta el POJO usado para leer/escribir el archivo Config.json
     */
    public static class Init {
        public String language;
        public Video video;
        public boolean resizeable;
        public boolean disableAudio;
        public boolean startMaximized;

        public String getLanguage() {
            return language;
        }

        void setLanguage(String language) {
            this.language = language;
        }

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
            public int width;
            public int height;
            public boolean vSync;
            public String HiDPI_Mode;

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

    public static class Account {
        public String email;
        public String password;
    }

    public static class Network {
        public Array<Server> servers;
        public int selected = -1;

        public Network() {
            servers = new Array<>();
        }

        public static class Server {
            public String desc;
            public String hostname;
            public int port;

            // empty constructor needed for de-serialization
            public Server() {
                this(null, "127.0.0.1", 7666);
            }

            public Server(String hostname, int port) {
                this(null, hostname, port);
            }

            public Server(String desc, String hostname, int port) {
                this.desc = desc;
                this.hostname = hostname;
                this.port = port;
            }

            @Override
            public String toString() {
                String prefix = "";
                if (this.desc != null)
                    prefix = this.desc + "  ";

                return prefix + this.hostname + ":" + this.port;
            }
        }
    }
}
