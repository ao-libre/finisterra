package ar.com.tamborindeguy.network.movement;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;
import physics.AOPhysics;
import position.WorldPos;

public class MovementRequest implements IRequest {

    public boolean valid;
    public int requestNumber;
    public WorldPos predicted;
    public AOPhysics.Movement movement;

    public MovementRequest(){}

    public MovementRequest(int requestNumber, WorldPos predicted, AOPhysics.Movement movement, boolean valid) {
        this.requestNumber = requestNumber;
        this.predicted = predicted;
        this.movement = movement;
        this.valid = valid;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
