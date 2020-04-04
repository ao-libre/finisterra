package shared.network.movement;

import component.position.WorldPos;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

import java.util.Objects;

public class MovementRequest implements IRequest {

    public boolean valid;
    public int requestNumber;
    public WorldPos predicted;
    public int movement;

    public MovementRequest() {
    }

    public MovementRequest(int requestNumber, WorldPos predicted, int movement, boolean valid) {
        this.requestNumber = requestNumber;
        this.predicted = predicted;
        this.movement = movement;
        this.valid = valid;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovementRequest that = (MovementRequest) o;
        return Objects.equals(predicted, that.predicted) &&
                movement == that.movement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicted, movement);
    }
}
