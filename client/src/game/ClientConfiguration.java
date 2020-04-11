package game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import game.utils.Resources;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.util.AOJson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Este es el POJO del archivo de configuracion del cliente [Config.json]
 * @see AOGame
 */
public class ClientConfiguration extends PassiveSystem {

    private Init initConfig;
    private Account account;
    private Network network;

    /**
     * Obtiene el archivo Config.json y lee el JSON.
     *
     * @param path La ruta donde se encuentra el Config.json a cargar.
     *
     * @throws FileNotFoundException Si no se encuentra el archivo.
	 * @throws IOException Si por alguna misteriosa razon no se puede leer el archivo.
     */
    public static ClientConfiguration loadConfig(String path) {
        
		Json configObject = new AOJson();      
		try (FileInputStream is = new FileInputStream(path)) {        
			// Before GDX initialization
            // DO NOT USE 'Gdx.Files', because 'Gdx.Files' in the launcher is always NULL!
            return configObject.fromJson(ClientConfiguration.class, is);
            
		} catch (FileNotFoundException ex) {
            Log.error("Client configuration" , "File not found!", ex);
        
		} catch (IOException ex) {
		    Log.error("Client configuration" , "It appears we've encountered an unexpected I/O error!", ex);
		}

        return null;
    }

    /**
     *  Crea un objeto con los valores de configuracion por defecto por si el archivo no existe.
     *
     * @return configOutput El objeto con los valores por defecto.
     */
    public static ClientConfiguration createConfig() {
        // Default values will not be written down
        ClientConfiguration configOutput = new ClientConfiguration();

        // Default values of `Init`
        configOutput.setInitConfig(new Init());
        Init initConfig = configOutput.getInitConfig();
        initConfig.setLanguage("es_AR");
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

        // Default values of `Account`
        Account account = new Account();
        account.setEmail("");
        account.setPassword("");
        configOutput.setAccount(account);

        // Default values of `Network`
        configOutput.setNetwork(new Network());

        // Default values of `Network.servers`
        Array<Network.Server> servers = configOutput.getNetwork().getServers();
        servers.add(new Network.Server("localhost", "127.0.0.1", 7666));

        return configOutput;
    }

    public void save() {
        save(Resources.CLIENT_CONFIG);
    }

    /**
     * Este metodo guarda el archivo Config.json.
     *
     * @see this#createConfig()
     * @param path La ruta donde queres que se guarde el Config.json generado.
    */
    public void save(String path) {
        Json json = new AOJson();
        json.toJson(this, new FileHandle(path));
    }

    public Init getInitConfig() {
        return initConfig;
    }

    private void setInitConfig(Init initConfig) {
        this.initConfig = initConfig;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * De aca en adelante esta el POJO usado para leer/escribir el archivo Config.json
     */
    public static class Init {
        private String language;
        private Video video;
        private boolean resizeable;
        private boolean disableAudio;
        private boolean startMaximized;

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

    public static class Account {
        public String email;
        public String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Network {
        Array<Server> servers;

        public Network() {
            servers = new Array<>();
        }

        public Array<Server> getServers() {
            return servers;
        }

        public static class Server {
            private String name;
            private String hostname;
            private int port;

            // empty constructor needed for de-serialization
            public Server() {
                this(null, "127.0.0.1", 7666);
            }

            public Server(String hostname, int port) {
                this(null, hostname, port);
            }

            public Server(String name, String hostname, int port) {
                this.name = name;
                this.hostname = hostname;
                this.port = port;
            }

            @Override
            public String toString() {
                String prefix = "";
                if (this.name != null)
                    prefix = this.name + "  ";

                return prefix + this.hostname + ":" + this.port;
            }

            public String getName() {
                return name;
            }

            void setName(String name) {
                this.name = name;
            }

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
