package shared.network.lobby;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class StartGameRequest implements IRequest {

    private int roomId;

    private StartGameRequest() {}

    public StartGameRequest(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
