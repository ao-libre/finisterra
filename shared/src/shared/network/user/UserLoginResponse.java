package shared.network.user;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class UserLoginResponse implements IResponse {

    private boolean ok = true;
    private String message;

    public UserLoginResponse() {
    }

    public static UserLoginResponse ok() {
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        return userLoginResponse;
    }

    public static UserLoginResponse failed(String message) {
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.ok = false;
        userLoginResponse.message = message;
        return userLoginResponse;
    }

    public boolean isSuccessful() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
