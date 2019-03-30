package server.core;

import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import server.manager.MapManager;
import server.manager.WorldManager;
import server.manager.NetworkManager;
import server.network.ServerNotificationProcessor;
import server.network.ServerRequestProcessor;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

import java.util.Optional;

public class ServerSystem extends MarshalSystem {

    private IRequestProcessor requestProcessor;
    private INotificationProcessor notificationProcessor;
    private Server server;

    public ServerSystem(Server server, MarshalStrategy strategy) {
        this(server, strategy, new ServerRequestProcessor(server), new ServerNotificationProcessor(server));
    }

    public ServerSystem(Server server, MarshalStrategy strategy, IRequestProcessor requestProcessor, INotificationProcessor notificationProcessor) {
        super(new NetworkDictionary(), strategy);
        this.server = server;
        this.requestProcessor = requestProcessor;
        this.notificationProcessor = notificationProcessor;
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
        getServer().ifPresent(server -> {
            if (!server.getNetworkManager().connectionHasPlayer(connectionId)) {
                return;
            }
            int playerToDisconnect = server.getNetworkManager().getPlayerByConnection(connectionId);
            server.getNetworkManager().unregisterUserConnection(playerToDisconnect, connectionId);
            server.getWorldManager().unregisterEntity(playerToDisconnect);
            server.getMapManager().removeEntity(playerToDisconnect);

        });
    }

    public Optional<Server> getServer() {
        return Optional.ofNullable(server);
    }
}
