package server.systems.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import server.configs.ServerConfiguration;
import server.core.ServerStrategy;
import server.systems.account.UserSystem;
import server.systems.config.ConfigurationSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.IRequest;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Wire
public class ServerSystem extends MarshalSystem {

    // Injected Systems
    private MapSystem mapSystem;
    private ServerNotificationProcessor notificationProcessor;
    private ServerRequestProcessor requestProcessor;
    private WorldEntitiesSystem worldEntitiesSystem;
    private ConfigurationSystem configurationSystem;
    private UserSystem userSystem;

    private Deque<NetworkJob> netQueue = new ConcurrentLinkedDeque<>();

    // @todo acá se podría usar BiMap de Google Guava
    // @see https://github.com/google/guava/wiki/NewCollectionTypesExplained#bimap
    private Map<Integer, Integer> playerByConnection = new ConcurrentHashMap<>();
    private Map<Integer, Integer> connectionByPlayer = new ConcurrentHashMap<>();

    public ServerSystem() {
        super(new NetworkDictionary(), new ServerStrategy());
    }

    @Override
    protected void initialize() {
        MarshalStrategy marshal = getMarshal();
        if (marshal instanceof ServerStrategy) {
            ServerConfiguration.Network.Ports ports = configurationSystem.getServerConfig().getNetwork().getPorts();
            ((ServerStrategy) marshal).prepare(ports.getTcpPort(), ports.getUdpPort());
        }
        start();
    }

    @Override
    public void received(int connectionId, Object object) {
        netQueue.add(new NetworkJob(connectionId, object));
    }

    private void processJob(NetworkJob job) {
        int connectionId = job.connectionId;
        Object object = job.receivedObject;
        try {
            if (object instanceof IRequest) {
                ((IRequest) object).accept(requestProcessor, connectionId);
            } else if (object instanceof INotification) {
                ((INotification) object).accept(notificationProcessor);
            }
        } catch (Exception e) {
            Log.error("Failed to process Job", e);
        }
    }

    @Override
    protected void processSystem() {
        super.processSystem();
        while (netQueue.peek() != null) {
            processJob(netQueue.poll());
        }
    }

    @Override
    public void disconnected(int connectionID) {
        super.disconnected(connectionID);
        if (connectionHasNoPlayer(connectionID)) {
            return;
        }
        Gdx.app.postRunnable(() -> {
            int playerID = getPlayerByConnection(connectionID);
            E player = E.E(playerID);
            String username = player.nameText();
            worldEntitiesSystem.unregisterEntity(getPlayerByConnection(connectionID));
            userSystem.logout(username);
        });
    }

    public void closeConnection(int connectionID) {
        ServerStrategy marshal = (ServerStrategy) getMarshal();
        marshal.getConnection(connectionID).close();
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

    public void registerUserConnection(int playerId, int connectionId) {
        playerByConnection.put(connectionId, playerId);
        connectionByPlayer.put(playerId, connectionId);
    }

    public void unregisterUserConnection(int playerId) {
        if (playerHasConnection(playerId)) {
            playerByConnection.remove(getConnectionByPlayer(playerId));
            connectionByPlayer.remove(playerId);
        }
    }

    public boolean connectionHasNoPlayer(int connectionId) {
        return !playerByConnection.containsKey(connectionId);
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

    public int getAmountConnections() {
        return connectionByPlayer.size();
    }

}

final class NetworkJob {

    final int connectionId;
    final Object receivedObject;

    NetworkJob(int connectionId, Object receivedObject) {
        this.connectionId = connectionId;
        this.receivedObject = receivedObject;
    }
}

