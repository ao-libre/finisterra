package shared.network.lobby;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class ExitRoomRequest implements IRequest {
    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
