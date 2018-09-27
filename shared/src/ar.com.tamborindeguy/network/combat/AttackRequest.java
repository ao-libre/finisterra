package ar.com.tamborindeguy.network.combat;

import ar.com.tamborindeguy.model.AttackType;
import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;
import physics.Attack;

public class AttackRequest implements IRequest {

    private AttackType type;

    public AttackRequest() {}

    public AttackRequest(AttackType type) {
        this.type = type;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
