package shared.network.account;

public class AccountLoginResponse {
    boolean successful;

    public AccountLoginResponse() {
    }

    public AccountLoginResponse(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
