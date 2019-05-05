package shared.network.combat;

import shared.model.AttackType;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class AttackRequest implements IRequest {

    private AttackType type;

    public AttackRequest() {
    }

    public AttackRequest(AttackType type) {
        this.type = type;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
