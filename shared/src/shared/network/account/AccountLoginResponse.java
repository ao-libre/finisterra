package shared.network.account;

public class AccountLoginResponse {
    boolean success;

    public AccountLoginResponse() {
    }

    public AccountLoginResponse(boolean success) {
        this.success = success;
    }

    public boolean success() {
        return success;
    }
}
