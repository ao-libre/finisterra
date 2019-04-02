package shared.network.lobby;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class CreateRoomRequest implements IRequest {

    private int maxPlayers;

    public CreateRoomRequest() {}

    public CreateRoomRequest(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
