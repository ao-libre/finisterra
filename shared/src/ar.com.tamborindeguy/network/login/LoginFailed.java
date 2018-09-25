package ar.com.tamborindeguy.network.login;

import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;

public class LoginFailed implements IResponse {

    private String reason;

    public LoginFailed() {
    }

    public LoginFailed(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
