package ar.com.tamborindeguy.core;

import ar.com.tamborindeguy.manager.MapManager;
import ar.com.tamborindeguy.manager.WorldManager;
import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.ServerNotificationProcessor;
import ar.com.tamborindeguy.network.ServerRequestProcessor;
import ar.com.tamborindeguy.network.init.NetworkDictionary;
import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;
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
