package server.core;

import com.esotericsoftware.kryonet.Server;
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
            System.out.print("Starting server... ");
            ((Server) endpoint).bind(tcpPort, udpPort);
            System.out.println("Server UP and listening");
            state = MarshalState.STARTED;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILED!");
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
