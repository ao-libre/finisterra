package server.core;

import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.kryonet.KryonetMarshalStrategy;

import java.io.IOException;

public class KryonetServerMarshalStrategy extends KryonetMarshalStrategy {

    private final int tcpPort;
    private final int udpPort;

    public KryonetServerMarshalStrategy(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        endpoint = new Server();
    }

    @Override
    protected void connectEndpoint() {
        try {
            Log.info("Starting server...");
            ((Server) endpoint).bind(tcpPort, udpPort);
            state = MarshalState.STARTED;
            Log.info("Server UP and LISTENING on ports TCP: " + tcpPort + " and UDP: " + udpPort);
        } catch (IOException e) {
            e.printStackTrace();
            Log.info("Error while connecting to the endpoint...");
            state = MarshalState.FAILED_TO_START;
        }
    }

    @Override
    public void sendToAll(Object o) {
        ((Server) endpoint).sendToAllTCP(o);
    }

    public void sendTo(int connectionId, Object o) {
        ((Server) endpoint).sendToTCP(connectionId, o);
    }

}
