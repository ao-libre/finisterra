package game.systems.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.kryonet.KryonetMarshalStrategy;

import java.io.IOException;

public class KryonetClientMarshalStrategy extends KryonetMarshalStrategy {

    protected static final int CONNECTION_TIMEOUT = 3000;
    private String address;
    private int port;

    public KryonetClientMarshalStrategy() {
        endpoint = new Client(8192, 8291);
        Log.set(Log.LEVEL_DEBUG);
    }

    public KryonetClientMarshalStrategy(String address, int port) {
        this();
        setHost(address, port);
    }

    public void setHost(String address, int port) {
        if (state != MarshalState.STOPPED) throw new IllegalStateException();
        this.address = address;
        this.port = port;
    }

    @Override
    protected void connectEndpoint() {
        try {
            ((Client) endpoint).connect(CONNECTION_TIMEOUT, address, port, port + 1);
            Log.debug("Network", "Connected to " + address + ":" + port);
            state = MarshalState.STARTED;
        } catch (IOException e) {
            Log.error("Network", "Failed to connect!", e);
            state = MarshalState.FAILED_TO_START;
        }
    }

    @Override
    public void start() {
        state = MarshalState.STARTING;
        registerDictionary();
        endpoint.addListener(listener); // can be safely called more than once.
        endpoint.start();
        connectEndpoint(); // blocking
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
