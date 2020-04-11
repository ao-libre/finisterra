package shared.network.lobby.player;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class ChangeTeamRequest implements IRequest {
    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
