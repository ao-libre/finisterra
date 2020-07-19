package shared.network.user;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class UserLoginResponse implements IResponse {

    private boolean ok = true;
    private String message;

    public UserLoginResponse() {
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull UserLoginResponse ok() {
        return new UserLoginResponse();
    }

    public static @NotNull UserLoginResponse failed(String message) {
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
