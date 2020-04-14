package shared.network.movement;

import component.movement.Destination;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class MovementNotification implements INotification {

    private int playerId;
    private Destination destination;

    public MovementNotification() {
    }

    public MovementNotification(int playerId, Destination destination) {
        this.playerId = playerId;
        this.destination = destination;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }

    public Destination getDestination() {
        return destination;
    }

    public int getPlayerId() {
        return playerId;
    }
}
