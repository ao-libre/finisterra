package ar.com.tamborindeguy.network.login;

import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;

public class LoginOK implements IResponse {

    public int entityId;

    public LoginOK() {}

    public LoginOK(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
