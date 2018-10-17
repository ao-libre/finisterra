package ar.com.tamborindeguy.client.network;

import ar.com.tamborindeguy.client.managers.WorldManager;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.ui.GUI;
import ar.com.tamborindeguy.network.interaction.DropItem;
import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.movement.MovementNotification;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import entity.character.info.Inventory;
import position.WorldPos;

import static com.artemis.E.E;

public class ClientNotificationProcessor implements INotificationProcessor {

    @Override
    public void defaultProcess(INotification notification) {
    }

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        if (!WorldManager.entityExsists(entityUpdate.entityId)) {
            Log.info("Network entity doesn't exists: " + entityUpdate.entityId + ". So we create it");
            Entity newEntity = GameScreen.getWorld().createEntity();
            WorldManager.registerEntity(entityUpdate.entityId, newEntity.getId());
            addComponentsToEntity(newEntity, entityUpdate);
        } else {
            Log.info("Network entity exists: " + entityUpdate.entityId + ". Updating");
            updateEntity(entityUpdate);
        }
    }

    @Override
    public void processNotification(RemoveEntity removeEntity) {
        Log.debug("Unregistering entity: " + removeEntity.entityId);
        WorldManager.unregisterEntity(removeEntity.entityId);
    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        E player = E(GameScreen.getPlayer());
        Inventory inventory = player.getInventory();
        inventoryUpdate.getUpdates().forEach((position, item) -> {
            inventory.set(position, item);
            if (item == null) {
                Log.info("Item removed from position: " + position);
            } else {
                Log.info("Item: " + item.objId + " updated in position: " + position);
                Log.info("Item equipped: " + item.equipped);
            }
        });
        GUI.getInventory().updateUserInventory();
    }

    @Override
    public void processNotification(DropItem dropItem) {
        defaultProcess(dropItem);
    }

    @Override
    public void processNotification(MovementNotification movementNotification) {
        int playerId = WorldManager.getNetworkedEntity(movementNotification.getPlayerId());
        E(playerId).movementAdd(movementNotification.getDestination());
    }

    private void addComponentsToEntity(Entity newEntity, EntityUpdate entityUpdate) {
        EntityEdit edit = newEntity.edit();
        for (Component component : entityUpdate.components) {
            Log.info("Adding component: " + component);
            edit.add(component);
        }
        E entity = E(newEntity.getId());
        if (entity.hasWorldPos()) {
            WorldPos worldPos = entity.getWorldPos();
            entity.pos2DX(worldPos.x);
            entity.pos2DY(worldPos.y);
        }
    }

    private void updateEntity(EntityUpdate entityUpdate) {
        int entityId = WorldManager.getNetworkedEntity(entityUpdate.entityId);
        Entity entity = WorldManager.getWorld().getEntity(entityId);
        EntityEdit edit = entity.edit();
        for (Component component : entityUpdate.components) {
            // this should replace if already exists
            edit.add(component);
        }
        for (Class remove : entityUpdate.toRemove) {
            // avoid NPEs beacouse of multithreading
            Gdx.app.postRunnable(() -> edit.remove(remove));
        }
    }
}
