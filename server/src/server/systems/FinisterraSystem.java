package server.systems;

import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import server.core.Finisterra;
import server.network.FinisterraRequestProcessor;
import shared.model.lobby.Player;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FinisterraSystem extends MarshalSystem {

    private IRequestProcessor requestProcessor;
    private INotificationProcessor notificationProcessor;
    private Finisterra finisterra;
    private Deque<NetworkJob> netDeque = new ConcurrentLinkedDeque<>();

    public FinisterraSystem(Finisterra finisterra, MarshalStrategy strategy) {
        super(new NetworkDictionary(), strategy);
        this.finisterra = finisterra;
        requestProcessor = new FinisterraRequestProcessor(finisterra);
        start();
    }

    @Override
    public void received(int connectionId, Object object) {
        netDeque.add(new NetworkJob(connectionId, object));
    }

    private void processJob(NetworkJob networkJob) {
        Object object = networkJob.receivedObject;
        if (object instanceof IRequest) {
            ((IRequest) object).accept(requestProcessor, networkJob.connectionId);
        }
    }


    @Override
    protected void processSystem() {
        super.processSystem();
        while (netDeque.peek() != null) {
            processJob(netDeque.poll());
        }
    }

    @Override
    public void disconnected(int connectionId) {
        super.disconnected(connectionId);
        Player player = finisterra.getNetworkManager().getPlayerByConnection(connectionId);
        finisterra.getLobby().playerDisconnected(player);
        finisterra.getNetworkManager().unregisterUserConnection(connectionId);
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
