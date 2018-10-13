package ar.com.tamborindeguy.network.inventory;

import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import entity.character.info.Inventory;

import java.util.HashMap;

public class InventoryUpdate implements INotification {

    private int id;

    public InventoryUpdate() {}

    public InventoryUpdate(int id) {
        this.id = id;
    }

    private HashMap<Integer, Inventory.Item> updates = new HashMap<>();

    public void add(int i, Inventory.Item item) {
        updates.put(i, item);
    }

    public void remove(int i) {
        updates.put(i, null);
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }


    public HashMap<Integer, Inventory.Item> getUpdates() {
        return updates;
    }

    public int getId() {
        return id;
    }
}
