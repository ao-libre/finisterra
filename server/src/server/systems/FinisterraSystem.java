package server.systems;

import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import server.core.ServerStrategy;
import server.network.FinisterraRequestProcessor;
import server.network.NetworkJob;
import shared.model.lobby.Player;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.IRequest;
import shared.network.lobby.ExitRoomRequest;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FinisterraSystem extends MarshalSystem {

    private FinisterraRequestProcessor requestProcessor;

    private Deque<NetworkJob> netDeque = new ConcurrentLinkedDeque<>();
    private Map<Integer, Player> playerByConnection = new HashMap<>();
    private Map<Player, Integer> connectionByPlayer = new HashMap<>();

    public FinisterraSystem(MarshalStrategy strategy) {
        super(new NetworkDictionary(), strategy);
        start();
    }

    @Override
    public void received(int connectionId, Object object) {
        netDeque.add(new NetworkJob(connectionId, object));
    }

    private void processJob(NetworkJob networkJob) {
        Object object = networkJob.getReceivedObject();
        if (object instanceof IRequest) {
            ((IRequest) object).accept(requestProcessor, networkJob.getConnectionId());
        }
    }

    @Override
    protected void processSystem() {
        super.processSystem();
        while (netDeque.peek() != null) {
            processJob(netDeque.poll());
        }
    }

    /**
     * Object will be serialized and sent using kryo
     *
     * @param id     connection ID
     * @param packet Object to send
     */
    public void sendTo(int id, Object packet) {
        ServerStrategy marshal = (ServerStrategy) getMarshal();
        marshal.sendTo(id, packet);
    }


    public void registerUserConnection(Player player, int connectionId) {
        playerByConnection.put(connectionId, player);
        connectionByPlayer.put(player, connectionId);
    }

    private void unregisterUserConnection(int connectionId) {
        Player player = playerByConnection.get(connectionId);
        playerByConnection.remove(connectionId);
        connectionByPlayer.remove(player);
    }

    public boolean connectionHasPlayer(int connectionId) {
        return playerByConnection.containsKey(connectionId);
    }

    public boolean playerHasConnection(Player player) {
        return connectionByPlayer.containsKey(player);
    }

    public Player getPlayerByConnection(int connectionId) {
        return playerByConnection.get(connectionId);
    }

    public int getConnectionByPlayer(Player player) {
        return connectionByPlayer.get(player);
    }


    @Override
    public void disconnected(int connectionId) {
        super.disconnected(connectionId);
        requestProcessor.processRequest(new ExitRoomRequest(), connectionId);
        unregisterUserConnection(connectionId);
    }

}
