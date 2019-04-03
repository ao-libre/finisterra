package shared.network.lobby;

import shared.interfaces.Hero;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class JoinLobbyRequest implements IRequest {

    private Hero hero;
    private String playerName;

    public JoinLobbyRequest() {}

    public JoinLobbyRequest(String playerName, Hero hero) {
        this.playerName = playerName;
        this.hero = hero;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Hero getHero() {
        return hero;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
