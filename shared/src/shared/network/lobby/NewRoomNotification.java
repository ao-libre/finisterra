package shared.network.lobby;

import shared.model.lobby.Room;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class NewRoomNotification implements INotification {

    private Room room;

    public NewRoomNotification() {}

    public NewRoomNotification(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}
