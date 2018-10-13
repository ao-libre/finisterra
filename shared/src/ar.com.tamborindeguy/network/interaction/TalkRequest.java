package ar.com.tamborindeguy.network.interaction;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;

public class TalkRequest implements IRequest {

    private String message;

    public TalkRequest() {}

    public TalkRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
