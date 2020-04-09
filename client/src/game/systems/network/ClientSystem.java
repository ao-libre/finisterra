package game.systems.network;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.screens.GameScreen;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.IResponse;

@Wire
public class ClientSystem extends MarshalSystem {

    private ClientResponseProcessor responseProcessor;
    private GameNotificationProcessor notificationProcessor;

    public ClientSystem(String host, int port) {
        super(new NetworkDictionary(), new KryonetClientMarshalStrategy(host, port));
    }

    /**
     * Recibimos y procesamos los datos del servidor.
     *
     * @param connectionId ID de la conexion.
     * @param object Objeto (+ datos, obvio) enviados por el servidor.
     */
    @Override
    public void received(int connectionId, Object object) {
        Gdx.app.postRunnable(() -> {
            Log.debug(object.toString());
            if (object instanceof IResponse) {
                ((IResponse) object).accept(responseProcessor);
            } else if (object instanceof INotification) {
                ((INotification) object).accept(notificationProcessor);
            } else if (object instanceof INotification[]) {
                INotification[] notifications = (INotification[]) object;
                for (INotification notification : notifications) {
                    notification.accept(notificationProcessor);
                }
            }
        });
    }

    /**
     * Nos desconectamos del servidor.
     *
     * @param connectionId ID de la conexion.
     */
    @Override
    public void disconnected(int connectionId) {
        super.disconnected(connectionId);
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof GameScreen) {
            Gdx.app.postRunnable(game::toLogin);
        }
    }

    public void setNotificationProcessor(GameNotificationProcessor notificationProcessor) {
        this.notificationProcessor = notificationProcessor;
    }

    public void setResponseProcessor(ClientResponseProcessor responseProcessor) {
        this.responseProcessor = responseProcessor;
    }

    public void send(Object object) {
        getKryonetClient().sendToAll(object);
    }

    public KryonetClientMarshalStrategy getKryonetClient() {
        return (KryonetClientMarshalStrategy) getMarshal();
    }
}
