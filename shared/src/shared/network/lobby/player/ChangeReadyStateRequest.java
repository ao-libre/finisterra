package shared.network.lobby.player;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class ChangeReadyStateRequest implements IRequest {
    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
