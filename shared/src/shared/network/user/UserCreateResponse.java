package shared.network.user;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class UserCreateResponse implements IResponse {

    private boolean ok = true;
    private String message;

    public UserCreateResponse() {
    }

    public static UserCreateResponse ok() {
        return new UserCreateResponse();
    }

    public static UserCreateResponse failed(String message) {
        UserCreateResponse userCreateResponse = new UserCreateResponse();
        userCreateResponse.ok = false;
        userCreateResponse.message = message;
        return userCreateResponse;
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
