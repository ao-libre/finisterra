package game.systems.network;

import com.badlogic.gdx.Gdx;
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

    public boolean login = true;

    public ClientSystem(String host, int port) {
        super(new NetworkDictionary(), new KryonetClientMarshalStrategy(host, port));
    }

//    public void login(String username, Hero hero) {
//        // TODO: Why thread? Could use a timeout but theoretically already connected & started.
//        // TODO: Is explicit getMarshal().update() necessary?
//        if (getMarshal().getState() == MarshalState.STARTED) {
//            new Thread(() -> {
//                sendLogin(username, hero);
//                while (isLoggingIn()) {
//                    getMarshal().update();
//                    // wait connection ok
//                }
//            }).start();
//        }
//    }

    private void sendLogin(String username, Hero hero) {
        getMarshal().sendToAll(new JoinLobbyRequest(username, hero));
    }

    public void loginFinished() {
        login = false;
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

    @Override
    public void stop() {
        super.stop();
        login = true;
        Gdx.app.log("ClientSystem", "Network client stopped.");
    }

    public KryonetClientMarshalStrategy getKryonetClient() {
        return (KryonetClientMarshalStrategy) getMarshal();
    }
}
