package shared.network.interaction;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class TalkRequest implements IRequest {

    private String message;

    public TalkRequest() {
    }

    public TalkRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid() {
        return message.length() < 128;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
