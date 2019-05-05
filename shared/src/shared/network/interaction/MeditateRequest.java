package shared.network.interaction;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class MeditateRequest implements IRequest {

    public MeditateRequest() {
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
