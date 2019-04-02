package server.manager;


import server.core.KryonetServerMarshalStrategy;
import shared.model.lobby.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains relation between connections and players. Communicate and send packets to users
 */
public class LobbyNetworkManager {

    private Map<Integer, Player> playerByConnection = new HashMap<>();
    private Map<Player, Integer> connectionByPlayer = new HashMap<>();

    private KryonetServerMarshalStrategy strategy;

    public LobbyNetworkManager(KryonetServerMarshalStrategy strategy) {
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

    public void registerUserConnection(Player player, int connectionId) {
        playerByConnection.put(connectionId, player);
        connectionByPlayer.put(player, connectionId);
    }

    public void unregisterUserConnection(int connectionId) {
        Player player = playerByConnection.get(connectionId);
        playerByConnection.remove(connectionId);
        connectionByPlayer.remove(player);
    }

    public boolean connectionHasPlayer(int connectionId) {
        return playerByConnection.containsKey(connectionId);
    }

    public boolean playerHasConnection(int player) {
        return connectionByPlayer.containsKey(player);
    }

    public Player getPlayerByConnection(int connectionId) {
        return playerByConnection.get(connectionId);
    }

    public int getConnectionByPlayer(Player player) {
        return connectionByPlayer.get(player);
    }

}
