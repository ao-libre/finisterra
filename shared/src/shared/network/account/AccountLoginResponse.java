package shared.network.account;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

import java.util.ArrayList;

public class AccountLoginResponse implements IResponse {

    // TODO add all players
    String username;
    boolean successful;
    ArrayList<String> characters;

    public AccountLoginResponse() {
    }

    public AccountLoginResponse(String username, boolean successful, ArrayList<String> characters) {
        this.username = username;
        this.successful = successful;
        this.characters = characters;
    }

    public String getUsername() { return username; }
    public boolean isSuccessful() {
        return successful;
    }
    public ArrayList<String> getCharacters(){
        return characters;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
