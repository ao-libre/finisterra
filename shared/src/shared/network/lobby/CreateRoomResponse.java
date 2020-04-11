package shared.network.lobby;

import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class CreateRoomResponse implements IResponse {

    private Room room;
    private Player player;
    private Status status;

    private CreateRoomResponse() {
    }

    public CreateRoomResponse(Room room, Player player) {
        this.room = room;
        this.player = player;
        this.status = room == null ? Status.MAX_ROOM_LIMIT : Status.CREATED;
    }

    public Room getRoom() {
        return room;
    }

    public Player getPlayer() {
        return player;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }

    public enum Status {
        CREATED,
        MAX_ROOM_LIMIT
    }
}
