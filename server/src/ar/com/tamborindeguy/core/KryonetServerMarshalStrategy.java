package ar.com.tamborindeguy.core;

import com.esotericsoftware.kryonet.Server;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.kryonet.KryonetMarshalStrategy;

import java.io.IOException;

public class KryonetServerMarshalStrategy extends KryonetMarshalStrategy {

    public static final int TCP_PORT = 7666;
    public static final int UDP_PORT = 7667;

    public KryonetServerMarshalStrategy() {
        endpoint = new Server() {
            @Override
            public void sendToTCP(int connectionID, Object object) {
                System.out.println("Sent message to " + connectionID);
                super.sendToTCP(connectionID, object);
            }

        };
    }

    @Override
    protected void connectEndpoint() {
        try {
            System.out.print("Starting server... ");
            ((Server)endpoint).bind(TCP_PORT, UDP_PORT);
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
        ((Server)endpoint).sendToAllTCP(o);
    }

    public void sendTo(int connectionId, Object o) {
        ((Server)endpoint).sendToTCP(connectionId, o);
    }

}