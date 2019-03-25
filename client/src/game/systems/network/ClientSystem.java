package game.systems.network;

import game.network.ClientNotificationProcessor;
import game.network.ClientResponseProcessor;
import game.network.KryonetClientMarshalStrategy;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.network.login.LoginRequest;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;

public class ClientSystem extends MarshalSystem {

    public static IResponseProcessor responseProcessor = new ClientResponseProcessor();
    public static INotificationProcessor notificationProcessor = new ClientNotificationProcessor();

    public boolean login = true;

    public ClientSystem() {
        super(new NetworkDictionary(), new KryonetClientMarshalStrategy());
    }

    public ClientSystem(MarshalStrategy client) {
        super(new NetworkDictionary(), client);
    }

    @Override
    public void start() {
        if (getMarshal().getState() != MarshalState.STARTED || getMarshal().getState() != MarshalState.STARTING) {
            super.start();
        }
    }

    public void login(String user, int classId) {
        // TODO: Why thread? Could use a timeout but theoretically already connected & started.
        // TODO: Is explicit getMarshal().update() necessary?
        if (getMarshal().getState() == MarshalState.STARTED) {
            new Thread(() -> {
                sendLogin(user, classId);
                while (isLoggingIn()) {
                    getMarshal().update();
                    // wait connection ok
                }
            }).start();
        }
    }

    private void sendLogin(String user, int classId) {
        getMarshal().sendToAll(new LoginRequest(user, classId));
    }

    private boolean isLoggingIn() {
        return login;
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
