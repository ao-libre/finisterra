package game.systems.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import game.network.ClientResponseProcessor;
import game.network.GameNotificationProcessor;
import game.network.KryonetClientMarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import shared.interfaces.Hero;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.lobby.JoinLobbyRequest;

public class ClientSystem extends MarshalSystem {

    public static IResponseProcessor responseProcessor = new ClientResponseProcessor();
    public static INotificationProcessor notificationProcessor = new GameNotificationProcessor();

    public ClientSystem(String host, int port) {
        super(new NetworkDictionary(), new KryonetClientMarshalStrategy(host, port));
    }

    @Override
    public void received(int connectionId, Object object) {
        Gdx.app.postRunnable(() -> {
            Log.info(object.toString());
            if (object instanceof IResponse) {
                ((IResponse) object).accept(responseProcessor);
            } else if (object instanceof INotification) {
                ((INotification) object).accept(notificationProcessor);
            }
        });
    }

    public KryonetClientMarshalStrategy getKryonetClient() {
        return (KryonetClientMarshalStrategy) getMarshal();
    }
}
