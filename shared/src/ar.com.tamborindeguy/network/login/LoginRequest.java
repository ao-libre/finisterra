package ar.com.tamborindeguy.network.login;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;

public class LoginRequest implements IRequest {

    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    // TODO dont use user and password. Instead, name and class.
    public String username;
    public int heroId;

    public LoginRequest() {
    }

    public LoginRequest(String username, int heroId) {
        this.username = username;
        this.heroId = heroId;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
