package shared.network.account;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class AccountCreationRequest implements IRequest {
    String username;
    String email;
    String hash;
    String salt;

    public AccountCreationRequest() {
    }

    public AccountCreationRequest(String username, String email, String hash, String salt) {
        this.username = username;
        this.email = email;
        this.hash = hash;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() { return salt; }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }

}
