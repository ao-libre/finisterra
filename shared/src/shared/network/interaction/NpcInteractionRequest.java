package shared.network.interaction;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class NpcInteractionRequest implements IRequest {

    private int targetEntity;

    public NpcInteractionRequest() {
    }

    public NpcInteractionRequest(int targetEntity) {
        this.targetEntity = targetEntity;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }

    public int getTargetEntity() {
        return targetEntity;
    }
}
