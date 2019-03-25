package shared.network.interaction;

import position.WorldPos;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class DropItem implements INotification {

    private int count;
    private WorldPos position;
    private int playerId;
    private int slot;

    public DropItem(){}

    public DropItem(int playerId, int slot, WorldPos position) {
        this(playerId, slot, 1, position);
    }

    public DropItem(int playerId, int slot, int count, WorldPos position) {
        this.playerId = playerId;
        this.slot = slot;
        this.count = count;
        this.position = position;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
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

    public int getPlayerId() {
        return playerId;
    }
}
