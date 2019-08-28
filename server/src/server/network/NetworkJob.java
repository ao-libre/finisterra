package server.network;

public class NetworkJob {

    final int connectionId;
    final Object receivedObject;

    public NetworkJob(int connectionId, Object receivedObject) {
        this.connectionId = connectionId;
        this.receivedObject = receivedObject;
    }

    public Object getReceivedObject() {
        return receivedObject;
    }

    public int getConnectionId() {
        return connectionId;
    }
}
