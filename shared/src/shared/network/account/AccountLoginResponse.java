package shared.network.account;

import org.jetbrains.annotations.NotNull;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;
import shared.util.Messages;

import java.util.ArrayList;

public class AccountLoginResponse implements IResponse {

    // TODO add all players
    String username;
    boolean successful;
    Messages error;
    ArrayList<String> characters;
    ArrayList<Integer> charactersData;

    public AccountLoginResponse() {
    }

    public AccountLoginResponse(Messages error) {
        this.successful = false;
        this.error = error;
    }

    public AccountLoginResponse(String username, ArrayList<String> characters, ArrayList<Integer> charactersData) {
        this.username = username;
        this.successful = true;
        this.characters = characters;
        this.charactersData = charactersData;
    }

    public String getUsername() {
        return username;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Messages getError() {
        return error;
    }

    public ArrayList<String> getCharacters() {
        return characters;
    }

    public ArrayList<Integer> getCharactersData() {
        return charactersData;
    }

    @Override
    public void accept(@NotNull IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
