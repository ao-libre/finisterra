package ar.com.tamborindeguy.client.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.kryonet.KryonetMarshalStrategy;

import java.io.IOException;

public class KryonetClientMarshalStrategy extends KryonetMarshalStrategy {

    protected static final int CONNECTION_TIMEOUT = 1000;
    private final String host;
    private final int port;

    public KryonetClientMarshalStrategy(String host, int port) {
        this.host = host;
        this.port = port;
        endpoint = new Client();
    }

    @Override
    protected void connectEndpoint() {
        try {
            ((Client)endpoint).connect(CONNECTION_TIMEOUT, host, port,port+1);
            Log.debug("Connection OK");
            state = MarshalState.STARTED;
        } catch (IOException e) {
            Log.info("Failed to connect");
            state = MarshalState.FAILED_TO_START;
        }
    }

    /** Establish connection / prepare to listen. */
    @Override
    public void start() {
        state = MarshalState.STARTING;
        registerDictionary();
        endpoint.addListener(listener); // can be safely called more than once.
        Log.info("add listener to " + this.getClass().getSimpleName() + " " +  listener);
        endpoint.start();
        new Thread(() -> connectEndpoint()).start();
    }

    @Override
    public MarshalState getState() {
        return state;
    }

    @Override
    public void sendToAll(Object o) {
        Log.info("Send object " + o);
        ((Client)endpoint).sendTCP(o);
    }
}
