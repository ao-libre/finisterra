package shared.network.user;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class UserLoginRequest implements IRequest {

    private String userName;

    public UserLoginRequest() {
    }

    public UserLoginRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
