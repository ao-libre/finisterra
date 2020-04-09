package shared.network.inventory;

import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class ItemActionRequest implements IRequest {

    private int slot;
    private int action;

    public ItemActionRequest() {
    }

    public ItemActionRequest(int slot, int action) {
        this.slot = slot;
        this.action = action;
    }

    public int getSlot() {
        return slot;
    }

    public int getAction() {
        return action;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }

    public enum ItemAction {
        USE,
        EQUIP
    }
}
