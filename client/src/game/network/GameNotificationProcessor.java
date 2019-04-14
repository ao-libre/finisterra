package game.network;

import camera.Focused;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import entity.character.info.Inventory;
import game.AOGame;
import game.managers.WorldManager;
import game.screens.GameScreen;
import game.screens.LobbyScreen;
import game.screens.RoomScreen;
import game.ui.GUI;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.lobby.JoinRoomNotification;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.network.notifications.RemoveEntity;

import static com.artemis.E.E;

public class GameNotificationProcessor extends DefaultNotificationProcessor {

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        if (!WorldManager.entityExsists(entityUpdate.entityId)) {
            Log.info("Network entity doesn't exists: " + entityUpdate.entityId + ". So we create it");
            Entity newEntity = GameScreen.getWorld().createEntity();
            WorldManager.registerEntity(entityUpdate.entityId, newEntity.getId());
            addComponentsToEntity(newEntity, entityUpdate);
            if (E(entityUpdate.entityId).entity().getComponent(Focused.class) != null) {
                GameScreen.setPlayer(entityUpdate.entityId);
                GUI.getSpellView().updateSpells();
            }
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
    public void processNotification(MovementNotification movementNotification) {
        int playerId = WorldManager.getNetworkedEntity(movementNotification.getPlayerId());
        E(playerId).aOPhysics();
        E(playerId).movementAdd(movementNotification.getDestination());
    }

    @Override
    public void processNotification(FXNotification fxNotification) {
        int target = WorldManager.getNetworkedEntity(fxNotification.getTarget());
        E(target).fXAddFx(fxNotification.getFxGrh());
    }

    private void addComponentsToEntity(Entity newEntity, EntityUpdate entityUpdate) {
        EntityEdit edit = newEntity.edit();
        for (Component component : entityUpdate.components) {
            Log.info("Adding component: " + component);
            edit.add(component);
        }
    }

    private void updateEntity(EntityUpdate entityUpdate) {
        int entityId = WorldManager.getNetworkedEntity(entityUpdate.entityId);
        Entity entity = WorldManager.getWorld().getEntity(entityId);
        Gdx.app.postRunnable(() -> {
            EntityEdit edit = entity.edit();
            for (Component component : entityUpdate.components) {
                // this should replace if already exists
                edit.add(component);
                Log.info("Adding component: " + component.toString());
            }
            for (Class remove : entityUpdate.toRemove) {
                Log.info("Removing component: " + remove.getSimpleName());
                edit.remove(remove);
            }
        });
    }

    @Override
    public void processNotification(JoinRoomNotification joinRoomNotification) {
        Gdx.app.postRunnable(() -> {
            AOGame game = (AOGame) Gdx.app.getApplicationListener();
            if (game.getScreen() instanceof RoomScreen) {
                RoomScreen room = (RoomScreen) game.getScreen();
                room.getRoom().add(joinRoomNotification.getPlayer());
            }
        });
        joinRoomNotification.getPlayer();
    }
}
