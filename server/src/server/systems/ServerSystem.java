package server.systems;

import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import server.core.Server;
import server.network.ServerNotificationProcessor;
import server.network.ServerRequestProcessor;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ServerSystem extends MarshalSystem {

    private IRequestProcessor requestProcessor;
    private INotificationProcessor notificationProcessor;
    private Server server;
    private Deque<NetworkJob> netQueue = new ConcurrentLinkedDeque<>();


    public ServerSystem(Server server, MarshalStrategy strategy) {
        this(server, strategy, new ServerRequestProcessor(server), new ServerNotificationProcessor(server));
    }

    public ServerSystem(Server server, MarshalStrategy strategy, IRequestProcessor requestProcessor,
                        INotificationProcessor notificationProcessor) {
        super(new NetworkDictionary(), strategy);
        this.server = server;
        this.requestProcessor = requestProcessor;
        this.notificationProcessor = notificationProcessor;
        start();
    }

    @Override
    public void received(int connectionId, Object object) {
        netQueue.add(new NetworkJob(connectionId, object));
    }

    private void processJob(NetworkJob job) {
        int connectionId = job.connectionId;
        Object object = job.receivedObject;
        if (object instanceof IRequest) {
            ((IRequest) object).accept(requestProcessor, connectionId);
        } else if (object instanceof INotification) {
            ((INotification) object).accept(notificationProcessor);
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
    public void disconnected(int connectionId) {
        super.disconnected(connectionId);
        getServer().ifPresent(server -> {
            if (!server.getNetworkManager().connectionHasPlayer(connectionId)) {
                return;
            }
            int playerToDisconnect = server.getNetworkManager().getPlayerByConnection(connectionId);
            server.getMapManager().removeEntity(playerToDisconnect);
            server.getNetworkManager().unregisterUserConnection(playerToDisconnect, connectionId);
            server.getWorldManager().unregisterEntity(playerToDisconnect);

        });
    }

    public Optional<Server> getServer() {
        return Optional.ofNullable(server);
    }

    private static class NetworkJob {

        private final int connectionId;
        private final Object receivedObject;

        private NetworkJob(int connectionId, Object receivedObject) {
            this.connectionId = connectionId;
            this.receivedObject = receivedObject;
        }
    }
}
