package shared.network.interaction;

import component.position.WorldPos;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class DropItem implements IRequest {

    private int count;
    private WorldPos position;
    private int slot;

    public DropItem() {
    }

    public DropItem(int slot, WorldPos position) {
        this(slot, 1, position);
    }

    public DropItem(int slot, int count, WorldPos position) {
        this.slot = slot;
        this.count = count;
        this.position = position;
    }


    public int getCount() {
        return count;
    }

    public int getSlot() {
        return slot;
    }

    public WorldPos getPosition() {
        return position;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}
