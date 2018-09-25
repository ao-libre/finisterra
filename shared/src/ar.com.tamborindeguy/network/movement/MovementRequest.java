package ar.com.tamborindeguy.network.movement;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;
import physics.AOPhysics;

public class MovementRequest implements IRequest {

    public boolean valid;
    public int requestNumber;
    public AOPhysics.Movement movement;

    public MovementRequest(){}

    public MovementRequest(int requestNumber, AOPhysics.Movement movement, boolean valid) {
        this.requestNumber = requestNumber;
        this.movement = movement;
        this.valid = valid;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
