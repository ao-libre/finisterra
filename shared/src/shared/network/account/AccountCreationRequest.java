package shared.network.account;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class AccountCreationRequest implements IRequest {
    String username;
    String email;
    String password;

    public AccountCreationRequest() {
    }

    public AccountCreationRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
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
