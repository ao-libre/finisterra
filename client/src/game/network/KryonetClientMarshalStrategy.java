package game.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.kryonet.KryonetMarshalStrategy;

import java.io.IOException;

public class KryonetClientMarshalStrategy extends KryonetMarshalStrategy {

    protected static final int CONNECTION_TIMEOUT = 3000;
    private String host;
    private int port;

    public KryonetClientMarshalStrategy(String host, int port) {
        this.host = host;
        this.port = port;
        endpoint = new Client(8192, 8192);
    }

    public void setHost(String host) {
        if (state == MarshalState.STOPPED)
            this.host = host;
    }

    public void setPort(int port) {
        if (state == MarshalState.STOPPED)
            this.port = port;
    }

    @Override
    protected void connectEndpoint() {
        try {
            ((Client) endpoint).connect(CONNECTION_TIMEOUT, host, port, port + 1);
            Log.info("Connected to " + host + ":" + port);
            state = MarshalState.STARTED;
        } catch (IOException e) {
            Log.info("Failed to connect!");
            state = MarshalState.FAILED_TO_START;
        }
    }

    /**
     * Establish connection / prepare to listen.
     */
    @Override
    public void start() {
        state = MarshalState.STARTING;
        registerDictionary();
        endpoint.addListener(listener); // can be safely called more than once.
        endpoint.start();
        connectEndpoint(); // Let it block! Let it block! Let it block! ♫
    }

    @Override
    public void stop() {
        super.stop();
        if (state == MarshalState.STOPPED) Log.debug("Disconnected!");
    }

    @Override
    public void sendToAll(Object o) {
        ((Client) endpoint).sendTCP(o);
    }
}
