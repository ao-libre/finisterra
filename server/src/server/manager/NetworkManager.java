package server.manager;


import server.core.KryonetServerMarshalStrategy;
import server.core.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains relation between connections and players. Communicate and send packets to users
 */
public class NetworkManager extends DefaultManager {

    private Map<Integer, Integer> playerByConnection = new HashMap<>();
    private Map<Integer, Integer> connectionByPlayer = new HashMap<>();

    private KryonetServerMarshalStrategy strategy;

    public NetworkManager(Server server, KryonetServerMarshalStrategy strategy) {
        super(server);
        this.strategy = strategy;
    }

    public void stop() {
        strategy.stop();
    }

    /**
     * Object will be serialized and sent using kryo
     *
     * @param id     connection ID
     * @param packet Object to send
     */
    public void sendTo(int id, Object packet) {
        strategy.sendTo(id, packet);
    }

    public void registerUserConnection(int playerId, int connectionId) {
        playerByConnection.put(connectionId, playerId);
        connectionByPlayer.put(playerId, connectionId);
    }

    public void unregisterUserConnection(int playerId, int connectionId) {
        playerByConnection.remove(connectionId, playerId);
        connectionByPlayer.put(playerId, connectionId);
    }

    public boolean connectionHasPlayer(int connectionId) {
        return playerByConnection.containsKey(connectionId);
    }

    public boolean playerHasConnection(int player) {
        return connectionByPlayer.containsKey(player);
    }

    public int getPlayerByConnection(int connectionId) {
        return playerByConnection.get(connectionId);
    }

    public int getConnectionByPlayer(int playerId) {
        return connectionByPlayer.get(playerId);
    }

    @Override
    public void init() {
    }
}
