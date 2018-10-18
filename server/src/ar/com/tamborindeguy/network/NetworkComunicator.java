package ar.com.tamborindeguy.network;


import ar.com.tamborindeguy.core.KryonetServerMarshalStrategy;

import java.util.HashMap;
import java.util.Map;

public class NetworkComunicator {

    private static Map<Integer, Integer> playerByConnection = new HashMap<>();
    private static Map<Integer, Integer> connectionByPlayer = new HashMap<>();

    private static KryonetServerMarshalStrategy server;
    private static NetworkComunicator instance;

    public NetworkComunicator(KryonetServerMarshalStrategy server) {
        NetworkComunicator.server = server;
        instance = this;
    }

    public void stop() {
        server.stop();
    }

    public static void sendTo(int id, Object packet) {
        server.sendTo(id, packet);
    }

    public static void sendToAll(Object packet) {
        server.sendToAll(packet);
    }

    public static void registerUserConnection(int playerId, int connectionId) {
        playerByConnection.put(connectionId, playerId);
        connectionByPlayer.put(playerId, connectionId);
    }

    public static void unregisterUserConnection(int playerId, int connectionId) {
        playerByConnection.remove(connectionId, playerId);
        connectionByPlayer.put(playerId, connectionId);
    }

    public static boolean connectionHasPlayer(int connectionId) {
        return playerByConnection.containsKey(connectionId);
    }

    public static boolean playerHasConnection(int player) {
        return connectionByPlayer.containsKey(player);
    }

    public static int getPlayerByConnection(int connectionId) {
        return playerByConnection.get(connectionId);
    }

    public static int getConnectionByPlayer(int playerId) {
        return connectionByPlayer.get(playerId);
    }
}
