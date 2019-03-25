package shared.network.interaction;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class TakeItemRequest implements IRequest {
    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
