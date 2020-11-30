package server.core;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.network.marshal.common.MarshalState;
import net.mostlyoriginal.api.network.marshal.kryonet.KryonetMarshalStrategy;

import java.io.IOException;

public class ServerStrategy extends KryonetMarshalStrategy {

    private int tcpPort;
    private int udpPort;

    public ServerStrategy() {
        endpoint = new Server();
        Log.set(Log.LEVEL_DEBUG);
    }

    public void prepare(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    @Override
    protected void connectEndpoint() {
        try {
            ((Server)endpoint).bind(tcpPort, udpPort);
            Log.info("Server initialization", "Listening connections in ports TCP: " + tcpPort + " and UDP: " + udpPort);
            state = MarshalState.STARTED;
        } catch (IOException e) {
            Log.error("Server initialization", "Server port binding has FAILED!", e);
            state = MarshalState.FAILED_TO_START;
        }
    }

    @Override
    public void sendToAll(Object o) {
        ((Server)endpoint).sendToAllTCP(o);
    }

    public void sendTo(int connectionID, Object o) {
        ((Server)endpoint).sendToTCP(connectionID, o);
    }

    public Connection getConnection(int connectionID) {
        Connection[] connections = ((Server)endpoint).getConnections();
        for (Connection connection : connections) {
            if (connection.getID() == connectionID) {
                return connection;
            }
        }
        return null;
    }
}
