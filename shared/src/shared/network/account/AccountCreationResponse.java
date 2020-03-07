package shared.network.account;

public class AccountCreationResponse {
    boolean success;

    public AccountCreationResponse() {
    }

    public AccountCreationResponse(boolean success) {
        this.success = success;
    }

    public boolean success() {
        return success;
    }
}
