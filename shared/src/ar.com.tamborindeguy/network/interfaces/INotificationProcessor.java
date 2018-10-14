package ar.com.tamborindeguy.network.interfaces;

import ar.com.tamborindeguy.network.interaction.DropItem;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;

public interface INotificationProcessor {

    void defaultProcess(INotification notification);

    void processNotification(EntityUpdate notification);

    void processNotification(RemoveEntity removeEntity);

    void processNotification(InventoryUpdate inventoryUpdate);

    void processNotification(DropItem dropItem);
}
