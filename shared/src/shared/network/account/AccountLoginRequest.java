package shared.network.account;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

/**
 * Account Login Request
 */
public class AccountLoginRequest implements IRequest {
    String email;
    String password;

    public AccountLoginRequest() {
    }

    public AccountLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
