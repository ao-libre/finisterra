package ar.com.tamborindeguy.network.inventory;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;

public class ItemActionRequest implements IRequest {

    private int slot;

    public ItemActionRequest() {}

    public ItemActionRequest(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
