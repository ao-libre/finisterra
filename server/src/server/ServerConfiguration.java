package server;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.minlog.Log;
import shared.util.AOJson;

public class ServerConfiguration {

    private Network network;
    private Rooms rooms;

    static ServerConfiguration loadConfig(String path) {
        Json configObject = new AOJson();
        try {
            // DO NOT USE 'Gdx.Files', because 'Gdx.Files' in the launcher is always NULL!
            return configObject.fromJson(ServerConfiguration.class, new FileHandle(path));

        } catch (Exception ex) {
            Log.debug("Server configuration file not found!");
        }

        return null;
    }

    // useful for client json sample creation
    static ServerConfiguration createConfig() {
        // Default values will not be written down
        ServerConfiguration configOutput = new ServerConfiguration();

        // Default values of `Network`
        configOutput.setNetwork(new Network());
        configOutput.getNetwork().setUseLocalHost(true);

        // Default values of `Network.Ports`
        Network.Ports defNetwork = new Network.Ports();
        defNetwork.setTcpPort(9000);
        defNetwork.setUdpPort(9001);
        configOutput.getNetwork().setPorts(defNetwork);

        // Default values of `Network.Api`
        Network.Api defApi = new Network.Api();
        defApi.setapiURL("https://localhost");
        defApi.setApiPort(1337);
        configOutput.getNetwork().setApi(defApi);

        // Default values for room creation
        configOutput.setRooms(new Rooms());
        configOutput.getRooms().setLimitCreation(1);

        return configOutput;
    }

    void save() {
        Json json = new AOJson();
        json.toJson(this, new FileHandle("Server.json"));
    }


    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public Rooms getRooms() {
        return this.rooms;
    }

    public void setRooms(Rooms rooms) {
        this.rooms = rooms;
    }

    public static class Network {
        private boolean useLocalHost;
        private Ports ports;
        private Api api;

        public boolean getuseLocalHost() {
            return this.useLocalHost;
        }

        private void setUseLocalHost(boolean useLocalHost) {
            this.useLocalHost = useLocalHost;
        }

        public Ports getPorts() {
            return ports;
        }

        void setPorts(Ports ports) {
            this.ports = ports;
        }

        public Api getApi() {
            return api;
        }

        void setApi(Api api) {
            this.api = api;
        }

        public static class Ports {
            private int tcpPort;
            private int udpPort;

            public int getTcpPort() {
                return tcpPort;
            }

            private void setTcpPort(int tcpPort) {
                this.tcpPort = tcpPort;
            }

            public int getUdpPort() {
                return udpPort;
            }

            private void setUdpPort(int udpPort) {
                this.udpPort = udpPort;
            }
        }

        public static class Api {
            private String apiURL;
            private int apiPort;

            public String getapiURL() {
                return this.apiURL;
            }

            private void setapiURL(String apiURL) {
                this.apiURL = apiURL;
            }

            public int getApiPort() {
                return apiPort;
            }

            private void setApiPort(int apiPort) {
                this.apiPort = apiPort;
            }
        }
    }

    public static class Rooms {
        private int limitCreation;

        public int getLimitCreation() {
            return limitCreation;
        }

        public void setLimitCreation(int limitCreation) {
            this.limitCreation = limitCreation;
        }
    }
}