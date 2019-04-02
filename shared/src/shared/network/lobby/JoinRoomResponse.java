package shared.network.lobby;

import shared.model.lobby.Player;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class JoinRoomResponse implements IResponse {

    private Player player;

    private JoinRoomResponse() {}

    public JoinRoomResponse(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
