package server.systems.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.minlog.Log;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import component.entity.character.info.Name;
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

    private final Deque<NetworkJob> netQueue = new ConcurrentLinkedDeque<>();
    private final BiMap<Integer, Integer> connectionTable = Maps.synchronizedBiMap(HashBiMap.create());

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
    public void received(int connectionID, Object object) {
        netQueue.add(new NetworkJob(connectionID, object));
    }

    private void processJob(NetworkJob job) {
        int connectionID = job.connectionID;
        Object object = job.receivedObject;
        try {
            if (object instanceof IRequest) {
                ((IRequest) object).accept(requestProcessor, connectionID);
            } else if (object instanceof INotification) {
                ((INotification) object).accept(notificationProcessor);
            } else if (object instanceof FrameworkMessage) {
                // Ignorar paquete interno a Kryonet
            } else {
                // @todo Cerrar conexión por paquete ilegal
                Log.warn("Received unidentified object: " + object.toString());
            }
        } catch (Exception e) {
            // @todo ¿cerrar conexión?
            Log.error("Failed to process object: " + object.toString(), e);
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
        if (connectionHasPlayer(connectionID)) {
            Gdx.app.postRunnable(() -> {
                int playerID = getPlayerByConnection(connectionID);
                Name username = E.E(playerID).getName();
                worldEntitiesSystem.unregisterEntity(playerID);
                userSystem.logout(username.text);
            });
        }
    }

    public void closeConnection(int connectionID) {
        ServerStrategy marshal = (ServerStrategy) getMarshal();
        marshal.getConnection(connectionID).close();
    }

    /**
     * Object will be serialized and sent using kryo
     *
     * @param connectionID
     * @param packet Object to send
     */
    public void sendTo(int connectionID, Object packet) {
        ServerStrategy marshal = (ServerStrategy) getMarshal();
        marshal.sendTo(connectionID, packet);
    }

    public void registerUserConnection(int connectionID, int playerID) {
        connectionTable.put(connectionID, playerID);
    }

    public void unregisterUserConnection(int playerID) {
        if (playerHasConnection(playerID)) {
            connectionTable.inverse().remove(playerID);
        }
    }

    public boolean connectionHasPlayer(int connectionID) {
        return connectionTable.containsKey(connectionID);
    }

    public boolean playerHasConnection(int playerID) {
        return connectionTable.inverse().containsKey(playerID);
    }

    public int getPlayerByConnection(int connectionID) {
        return connectionTable.get(connectionID);
    }

    public int getConnectionByPlayer(int playerID) {
        return connectionTable.inverse().get(playerID);
    }

    public int getAmountConnections() {
        return connectionTable.size();
    }
}

final class NetworkJob {
    final int connectionID;
    final Object receivedObject;

    NetworkJob(int connectionID, Object receivedObject) {
        this.connectionID = connectionID;
        this.receivedObject = receivedObject;
    }
}

