package game.systems.network;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import game.screens.GameScreen;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.system.MarshalSystem;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.IResponse;
import shared.network.user.UserLogoutRequest;

@Wire
public class ClientSystem extends MarshalSystem {

    private ClientResponseProcessor responseProcessor;
    private GameNotificationProcessor notificationProcessor;
    private ScreenManager screenManager;

    public ClientSystem() {
        super(new NetworkDictionary(), new KryonetClientMarshalStrategy());
    }

    public ClientSystem(String address, int port) {
        super(new NetworkDictionary(), new KryonetClientMarshalStrategy(address, port));
    }

    /**
     * Recibimos y procesamos los datos del servidor.
     *
     * @param connectionId ID de la conexion.
     * @param object       Objeto (+ datos, obvio) enviados por el servidor.
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
        if (screenManager.getScreen() instanceof GameScreen) {
            Gdx.app.postRunnable(() -> screenManager.to(ScreenEnum.LOGIN));
        }
    }

    public void logout(){
        send(new UserLogoutRequest());
    }

    public void disconnect(){
        getKryonetClient().getClient().close();
        stop();
        Log.info( getState() + "" );
    }

    public boolean connect() {
        // Inicializamos la conexion.
        start();
        return (getState() == MarshalState.STARTED);
    }

    public boolean connect(String address, int port) {
        if (getState() != MarshalState.STOPPED) {
            stop();
        }
        // Seteamos la info. del servidor al que nos vamos a conectar.
        setHost(address, port);
        return connect();
    }

    public void send(Object object) {
        getKryonetClient().sendToAll(object);
    }

    public void setHost(String address, int port) {
        getKryonetClient().setHost(address, port);
    }

    @Override
    protected void dispose() {
        try {
            getKryonetClient().getClient().stop(); // Cierra el thread de Kryonet
            getKryonetClient().getClient().dispose(); // Libera recursos
        } catch (Exception ignored) {
        }
    }

    private KryonetClientMarshalStrategy getKryonetClient() {
        return (KryonetClientMarshalStrategy) getMarshal();
    }
}
