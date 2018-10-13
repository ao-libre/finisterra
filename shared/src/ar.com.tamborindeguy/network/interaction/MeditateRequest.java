package ar.com.tamborindeguy.network.interaction;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;

public class MeditateRequest implements IRequest {

    public MeditateRequest() {}
    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
