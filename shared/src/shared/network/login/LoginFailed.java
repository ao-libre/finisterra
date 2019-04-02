package shared.network.login;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

@Deprecated
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
