package shared.network.account;

public class AccountCreationResponse {
    boolean successful;

    public AccountCreationResponse() {
    }

    public AccountCreationResponse(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
