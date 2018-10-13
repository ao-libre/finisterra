package ar.com.tamborindeguy.network.inventory;

import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;

public class ItemAction implements IRequest {

    private int objectId;

    public ItemAction(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
