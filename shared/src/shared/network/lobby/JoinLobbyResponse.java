package shared.network.lobby;

import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class JoinLobbyResponse implements IResponse {

    private Player player;
    private Room[] rooms;

    public JoinLobbyResponse() {
    }

    public JoinLobbyResponse(Player player, Room[] rooms) {
        this.player = player;
        this.rooms = rooms;
    }

    public Player getPlayer() {
        return player;
    }

    public Room[] getRooms() {
        return rooms;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
