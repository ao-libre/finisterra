package shared.network.user;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class UserLogoutResponse implements IResponse {

    private boolean ok = true;
    private String message;

    public UserLogoutResponse() {

    }
    @Contract(value = " -> new", pure = true)
    public static @NotNull UserLogoutResponse ok() {
        return new UserLogoutResponse();
    }
    public static @NotNull UserLogoutResponse failed(String message) {
        UserLogoutResponse userLogoutResponse = new UserLogoutResponse();
        userLogoutResponse.ok = false;
        userLogoutResponse.message = message;
        return userLogoutResponse;
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
