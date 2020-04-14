package shared.network.movement;

import component.position.WorldPos;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class MovementResponse implements IResponse {

    public int requestNumber;
    public WorldPos destination;

    public MovementResponse() {
    }

    public MovementResponse(int requestNumber, WorldPos pos) {
        this.requestNumber = requestNumber;
        this.destination = pos;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
