package shared.network.combat;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class AttackResponse implements IResponse {

    private String text;

    public AttackResponse() {}

    public AttackResponse(String text) {
        this.text = text;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
