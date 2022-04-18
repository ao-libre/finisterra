package server.configs;

public class ServerConfiguration extends BaseConfiguration {

    // Default values
    public static final String PATH = "Server.json";

    private static final int TCP_PORT = 9000;
    private static final int UDP_PORT = 9001;
    private static final String API_URL = "https://localhost";
    private static final int API_PORT = 1337;
    private static final int ROOM_LIMIT = 1;

    private Network network;
    private Rooms rooms;

    public ServerConfiguration() {
        super(PATH);
    }

    @Override
    public void loadDefaultValues() {
        // Default values of `Network`
        setNetwork(new Network());
        getNetwork().setUseLocalHost(true);

        // Default values of `Network.Ports`
        ServerConfiguration.Network.Ports defNetwork = new Network.Ports();
        defNetwork.setTcpPort(TCP_PORT);
        defNetwork.setUdpPort(UDP_PORT);

        network.setPorts(defNetwork);

        // Default values of `Network.Api`
        ServerConfiguration.Network.Api defApi = new ServerConfiguration.Network.Api();
        defApi.setapiURL(API_URL);
        defApi.setApiPort(API_PORT);
        network.setApi(defApi);

        // Default values for room creation
        setRooms(new ServerConfiguration.Rooms());
        getRooms().setLimitCreation(ROOM_LIMIT);
    }

    public Network getNetwork() {
        return network;
    }

    private void setNetwork(Network network) {
        this.network = network;
    }

    public Rooms getRooms() {
        return this.rooms;
    }

    private void setRooms(Rooms rooms) {
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

        private void setPorts(Ports ports) {
            this.ports = ports;
        }

        public Api getApi() {
            return api;
        }

        private void setApi(Api api) {
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
        private int maxPlayers;

        public int getLimitCreation() {
            return limitCreation;
        }

        private void setLimitCreation(int limitCreation) {
            this.limitCreation = limitCreation;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        private void setMaxPlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

    }
}