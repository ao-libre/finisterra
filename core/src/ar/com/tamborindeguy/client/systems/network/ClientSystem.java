package ar.com.tamborindeguy.client.systems.network;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.client.network.ClientNotificationProcessor;
import ar.com.tamborindeguy.client.network.ClientResponseProcessor;
import ar.com.tamborindeguy.network.init.NetworkDictionary;
import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;
import net.mostlyoriginal.api.network.system.MarshalSystem;

public class ClientSystem extends MarshalSystem {

    public static IResponseProcessor responseProcessor;
    public static INotificationProcessor notificationProcessor;

    public ClientSystem(AO ao) {
        super(new NetworkDictionary(), ao.getClient());
        responseProcessor = new ClientResponseProcessor(ao);
        notificationProcessor = new ClientNotificationProcessor(ao);
        start();
    }

    @Override
    public void connected(int connectionId) {
        super.connected(connectionId);
    }

    @Override
    public void received(int connectionId, Object object) {
        if (object instanceof IResponse) {
            ((IResponse) object).accept(responseProcessor);
        } else if (object instanceof INotification) {
            ((INotification) object).accept(notificationProcessor);
        }
    }
}
