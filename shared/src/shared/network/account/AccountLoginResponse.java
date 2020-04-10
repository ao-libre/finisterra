package shared.network.account;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class AccountLoginResponse implements IResponse {

    // TODO add all players
    String username;
    boolean successful;

    public AccountLoginResponse() {
    }

    public AccountLoginResponse(String username, boolean successful) {
        this.username = username;
        this.successful = successful;
    }

    public String getUsername() { return username; }
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
