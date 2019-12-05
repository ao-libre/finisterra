package server.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import entity.character.info.Inventory;
import physics.AttackAnimation;
import server.systems.ServerSystem;
import server.systems.manager.ItemManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
import shared.network.interaction.AddItem;
import shared.network.interaction.DropItem;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.objects.types.Obj;
import shared.util.Messages;

import static com.artemis.E.E;

@Wire
public class ServerNotificationProcessor extends DefaultNotificationProcessor {

    private WorldManager worldManager;
    private ItemManager itemManager;
    private ObjectManager objectManager;
    private ServerSystem networkManager;

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        worldManager.notifyToNearEntities(entityUpdate.entityId, entityUpdate);
    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        E player = E(inventoryUpdate.getId());
        Inventory inventory = player.getInventory();
        inventoryUpdate.getUpdates().forEach(inventory::set);
    }

    @Override
    public void processNotification(DropItem dropItem) {
        int slot = dropItem.getSlot();
        E entity = E(dropItem.getPlayerId());
        // remove item from inventory
        InventoryUpdate update = new InventoryUpdate();
        Inventory inventory = entity.getInventory();
        Inventory.Item item = inventory.items[slot];
        if (item == null) {
            return;
        }
        if (item.equipped) {
            itemManager.getItemConsumers().TAKE_OFF.accept(dropItem.getPlayerId(), objectManager.getObject(item.objId).get());
            item.equipped = false;
        }
        item.count -= dropItem.getCount();
        if (item.count <= 0) {
            inventory.remove(slot);
        }
        update.add(slot, inventory.items[slot]); // should remove item if count <= 0
        networkManager.sendTo(networkManager.getConnectionByPlayer(dropItem.getPlayerId()), update);
        // add new obj entity to world
        int object = world.create();
        E(object).worldPos()
                .worldPosMap(dropItem.getPosition().map)
                .worldPosX(dropItem.getPosition().x)
                .worldPosY(dropItem.getPosition().y);
        E(object).objectIndex(item.objId);
        E(object).objectCount(dropItem.getCount());
        worldManager.registerEntity(object);
    }

    @Override
    public void processNotification(AddItem addItem){

        int userId = addItem.getPlayerId();
        E entity = E(userId);
        Obj obj = addItem.getObj();
        int objId = obj.getId();

        InventoryUpdate update = new InventoryUpdate();
        Inventory inventory = entity.getInventory();

        int index = -1;
        int fistEmptySlot = -1;

        for (int i = 0; i<20 ; i++) {
            if(inventory.items[i] != null) {
                if(inventory.items[i].objId == objId) {
                    index = i;
                }else if(inventory.items[i].objId == 0){
                    if (fistEmptySlot == -1) {
                        fistEmptySlot = i;
                    }
                }
            } else {
                if (fistEmptySlot == -1) {
                    fistEmptySlot = i;
                }
            }
        }

        if (index != -1){
            inventory.items[index].count++;
            update.add(index, inventory.items[index]);
            networkManager.sendTo(networkManager.getConnectionByPlayer(addItem.getPlayerId()), update);
            worldManager.sendEntityUpdate(userId, ConsoleMessage.info(Messages.GOT_ITEM, obj.getName()));

        }else if ( fistEmptySlot != -1){
            inventory.add( objId,1,false );
            update.add(fistEmptySlot, inventory.items[fistEmptySlot]);
            networkManager.sendTo(networkManager.getConnectionByPlayer(addItem.getPlayerId()), update);
            worldManager.sendEntityUpdate(userId, ConsoleMessage.info(Messages.GOT_ITEM, obj.getName()));

        }else {
            worldManager.sendEntityUpdate(userId, ConsoleMessage.info(Messages.FULL_INVENTORY ));
        }
    }

}
