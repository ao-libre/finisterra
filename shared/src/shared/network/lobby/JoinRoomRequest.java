package shared.network.lobby;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class JoinRoomRequest implements IRequest {

    private int id;

    public JoinRoomRequest() {}

    public JoinRoomRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
