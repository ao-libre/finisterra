package game.systems.network;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.entity.character.info.Bag;
import game.systems.PlayerSystem;
import game.systems.camera.CameraShakeSystem;
import game.systems.resources.SoundsSystem;
import game.systems.ui.UserInterfaceSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.world.NetworkedEntitySystem;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;

import static com.artemis.E.E;
import static shared.network.notifications.EntityUpdate.NO_ENTITY;

@Wire
public class GameNotificationProcessor extends DefaultNotificationProcessor {

    private CameraShakeSystem cameraShakeSystem;
    private InventorySystem inventorySystem;
    private NetworkedEntitySystem networkedEntitySystem;
    private PlayerSystem playerSystem;
    private SoundsSystem soundsSystem;
    private UserInterfaceSystem userInterfaceSystem;

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        if (entityUpdate instanceof RemoveEntity) {
            if (networkedEntitySystem.exists(entityUpdate.entityId)) {
                networkedEntitySystem.unregisterEntity(entityUpdate.entityId);
            } else {
                Log.debug("This should never happen");
            }
            return;
        }
        if (entityUpdate.entityId == NO_ENTITY) {
            Entity noneEntity = world.createEntity();
            addComponentsToEntity(noneEntity, entityUpdate);
        } else if (!networkedEntitySystem.exists(entityUpdate.entityId)) {
            Log.debug("Network entity doesn't exist: " + entityUpdate.entityId + ". So we create it");
            Entity newEntity = getWorld().createEntity();
            networkedEntitySystem.registerEntity(entityUpdate.entityId, newEntity.getId());
            addComponentsToEntity(newEntity, entityUpdate);
        } else {
            Log.debug("Network entity exists: " + entityUpdate.entityId + ". Updating");
            updateEntity(entityUpdate);
        }

    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        E player = playerSystem.get();
        Bag bag = player.getBag();
        inventoryUpdate.getUpdates().forEach((position, item) -> {
            bag.set(position, item);
            if (item == null) {
                Log.debug("Item removed from position: " + position);
            } else {
                Log.debug("Item: " + item.objId + " updated in position: " + position);
                Log.debug("Item equipped: " + item.equipped);
            }
        });
        inventorySystem.update(bag);
    }

    @Override
    public void processNotification(MovementNotification movementNotification) {
        if (networkedEntitySystem.exists(movementNotification.getPlayerId())) {
            int playerId = networkedEntitySystem.getLocalId(movementNotification.getPlayerId());
            E(playerId).movementAdd(movementNotification.getDestination());
        }
    }

    private void addComponentsToEntity(Entity newEntity, EntityUpdate entityUpdate) {
        EntityEdit edit = newEntity.edit();
        Component[] components = entityUpdate.components;
        if (components != null) {
            addComponents(edit, components);
        }
    }

    private void updateEntity(EntityUpdate entityUpdate) {
        int entityId = networkedEntitySystem.getLocalId(entityUpdate.entityId);
        Entity entity = world.getEntity(entityId);
        EntityEdit edit = entity.edit();
        addComponents(edit, entityUpdate.components);
        for (Class remove : entityUpdate.toRemove) {
            edit.remove(remove);
        }
    }

    private void addComponents(EntityEdit edit, Component[] components) {
        for (Component component : components) {
            // this should replace if already exists
            edit.add(component);
        }
    }

}
