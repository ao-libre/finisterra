package ar.com.tamborindeguy.network.combat;

import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;

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
