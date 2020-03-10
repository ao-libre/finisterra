package shared.network.account;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class AccountLoginResponse implements IResponse {
    boolean successful;

    public AccountLoginResponse() {
    }

    public AccountLoginResponse(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
