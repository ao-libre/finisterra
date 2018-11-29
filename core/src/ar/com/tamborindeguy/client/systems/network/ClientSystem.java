package ar.com.tamborindeguy.client.systems.network;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.client.network.ClientNotificationProcessor;
import ar.com.tamborindeguy.client.network.ClientResponseProcessor;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.network.init.NetworkDictionary;
import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;
import ar.com.tamborindeguy.network.login.LoginRequest;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.common.MarshalStrategy;
import net.mostlyoriginal.api.network.system.MarshalSystem;

public class ClientSystem extends MarshalSystem {

    public static IResponseProcessor responseProcessor = new ClientResponseProcessor();
    public static INotificationProcessor notificationProcessor = new ClientNotificationProcessor();

    public boolean login = true;
    public boolean requestSent = false;

    public ClientSystem(MarshalStrategy client) {
        super(new NetworkDictionary(), client);
        start();
    }

    @Override
    public void start() {
        if (getMarshal().getState() != MarshalState.STARTED || getMarshal().getState() != MarshalState.STARTING) {
            super.start();
        }
    }

    public void login(AO game, String user, int classId) {
        new Thread(() -> {
            while (isLoggingIn()) {
                getMarshal().update();
                // wait connection ok
                if (!requestSent) {
                    switch (getMarshal().getState()) {
                        case STARTED:
                            requestSent = true;
                            sendLogin(game, user, classId);
                            break;
                        case FAILED_TO_START:
                            // show ui message that failed
                            return;
                    }
                }
            }
        }).start();
    }

    private void sendLogin(AO game, String user, int classId) {
        Gdx.app.postRunnable(() -> {
            GameScreen gameScreen = new GameScreen(game, this);
            game.setGameScreen(gameScreen);
            getMarshal().sendToAll(new LoginRequest(user, classId));
        });
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
}
