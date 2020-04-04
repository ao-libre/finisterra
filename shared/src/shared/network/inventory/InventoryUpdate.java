package shared.network.inventory;

import component.entity.character.info.Bag;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

import java.util.HashMap;

public class InventoryUpdate implements INotification {

    private int id;
    private HashMap<Integer, Bag.Item> updates = new HashMap<>();

    public InventoryUpdate() {
    }

    public InventoryUpdate(int id) {
        this.id = id;
    }

    public void add(int i, Bag.Item item) {
        updates.put(i, item);
    }

    public void remove(int i) {
        updates.put(i, null);
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }


    public HashMap<Integer, Bag.Item> getUpdates() {
        return updates;
    }

    public int getId() {
        return id;
    }
}
