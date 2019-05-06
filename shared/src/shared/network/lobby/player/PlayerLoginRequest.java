package shared.network.lobby.player;

import shared.model.lobby.Player;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class PlayerLoginRequest implements IRequest {

    private Player player;

    private PlayerLoginRequest() {
    }

    public PlayerLoginRequest(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
