package ar.com.tamborindeguy.network.movement;

import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;
import position.WorldPos;

public class MovementResponse implements IResponse {

    public int requestNumber;
    public WorldPos destination;

    public MovementResponse() {}
    public MovementResponse(int requestNumber, WorldPos pos) {
        this.requestNumber = requestNumber;
        this.destination = pos;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
