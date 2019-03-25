package server.core;

import server.manager.MapManager;
import server.manager.WorldManager;
import server.network.NetworkComunicator;
import server.network.ServerNotificationProcessor;
import server.network.ServerRequestProcessor;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;

public class ServerSystem extends MarshalSystem {

    private static IRequestProcessor requestProcessor = new ServerRequestProcessor();
    private static INotificationProcessor notificationProcessor = new ServerNotificationProcessor();

    public ServerSystem(MarshalStrategy strategy) {
        super(new NetworkDictionary(), strategy);
        start();
    }

    @Override
    public void received(int connectionId, Object object) {
        if (object instanceof IRequest) {
            ((IRequest) object).accept(requestProcessor, connectionId);
        } else if (object instanceof INotification) {
            ((INotification) object).accept(notificationProcessor);
        }
    }

    @Override
    public void disconnected(int connectionId) {
        super.disconnected(connectionId);
        if (!NetworkComunicator.connectionHasPlayer(connectionId)) {
            return;
        }
        int playerToDisconnect = NetworkComunicator.getPlayerByConnection(connectionId);
        NetworkComunicator.unregisterUserConnection(playerToDisconnect, connectionId);
        WorldManager.unregisterEntity(playerToDisconnect);
        MapManager.removeEntity(playerToDisconnect);
    }
}
