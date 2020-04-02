package game.systems.network;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import entity.character.info.Bag;
import game.AOGame;
import game.screens.LobbyScreen;
import game.screens.RoomScreen;
import game.systems.PlayerSystem;
import game.systems.camera.CameraShakeSystem;
import game.systems.resources.SoundsSystem;
import game.systems.ui.UserInterfaceSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.world.NetworkedEntitySystem;
import shared.model.lobby.Player;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.lobby.JoinRoomNotification;
import shared.network.lobby.NewRoomNotification;
import shared.network.lobby.player.ChangePlayerNotification;
import shared.network.movement.MovementNotification;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;

import static com.artemis.E.E;

@Wire
public class GameNotificationProcessor extends DefaultNotificationProcessor {

    private NetworkedEntitySystem networkedEntitySystem;
    private CameraShakeSystem cameraShakeSystem;
    private SoundsSystem soundsSystem;
    private UserInterfaceSystem userInterfaceSystem;
    private PlayerSystem playerSystem;
    private InventorySystem inventorySystem;

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        if (!networkedEntitySystem.exists(entityUpdate.entityId)) {
            Log.debug("Network entity doesn't exist: " + entityUpdate.entityId + ". So we create it");
            Entity newEntity = getWorld().createEntity();
            networkedEntitySystem.registerEntity(entityUpdate.entityId, newEntity.getId());
            addComponentsToEntity(newEntity, entityUpdate);
        } else {
            Log.debug("Network entity exists: " + entityUpdate.entityId + ". Updating");
            if (entityUpdate instanceof RemoveEntity) {
                networkedEntitySystem.unregisterEntity(entityUpdate.entityId);
                return;
            } else {
                updateActions(entityUpdate.entityId, () -> updateEntity(entityUpdate));
            }
        }
        int localEntity = networkedEntitySystem.get(entityUpdate.entityId);
        E localE = E(localEntity);
        if (localE != null && localE.hasRef()) {
            // Map ref to local entity
            localE.refId(networkedEntitySystem.get(localE.refId()));
        }
    }

    private void updateActions(int id, Runnable update) {
        // TODO move to cameraShakeSystem
        if (networkedEntitySystem.exists(id)) {
            int networkedEntity = networkedEntitySystem.get(id);
            if (networkedEntity == playerSystem.get().id()) {
                E e = E(networkedEntity);
                int preHealth = e.getHealth().min;
                update.run();
                onDamage(networkedEntity, preHealth);
            } else {
                update.run();
            }
        }
    }

    private void onDamage(int id, int preHealth) {
        // TODO move to cameraShakeSystem
        int postHealth = E(id).getHealth().min;
        if (postHealth < preHealth) {
            Log.info("Shake camera by " + (preHealth - postHealth));
            cameraShakeSystem.shake((preHealth - postHealth) / 10f);
            cameraShakeSystem.push(5, 5);
        }
    }

    @Override
    public void processNotification(RemoveEntity removeEntity) {
        Log.info("Unregistering entity: " + removeEntity.entityId);
        networkedEntitySystem.unregisterEntity(removeEntity.entityId);
    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        E player = playerSystem.get();
        Bag bag = player.getBag();
        inventoryUpdate.getUpdates().forEach((position, item) -> {
            bag.set(position, item);
            if (item == null) {
                Log.info("Item removed from position: " + position);
            } else {
                Log.info("Item: " + item.objId + " updated in position: " + position);
                Log.info("Item equipped: " + item.equipped);
            }
        });
        inventorySystem.update(bag);
    }

    @Override
    public void processNotification(MovementNotification movementNotification) {
        if (networkedEntitySystem.exists(movementNotification.getPlayerId())) {
            int playerId = networkedEntitySystem.get(movementNotification.getPlayerId());
            E(playerId).movementAdd(movementNotification.getDestination());
        }
    }

    private void addComponentsToEntity(Entity newEntity, EntityUpdate entityUpdate) {
        EntityEdit edit = newEntity.edit();
        for (Component component : entityUpdate.components) {
            edit.add(component);
        }
    }

    private void updateEntity(EntityUpdate entityUpdate) {
        int entityId = networkedEntitySystem.get(entityUpdate.entityId);
        Entity entity = world.getEntity(entityId);
        EntityEdit edit = entity.edit();
        for (Component component : entityUpdate.components) {
            // this should replace if already exists
            edit.add(component);
        }
        for (Class remove : entityUpdate.toRemove) {
            edit.remove(remove);
        }
    }

    @Override
    public void processNotification(JoinRoomNotification joinRoomNotification) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof RoomScreen) {
            RoomScreen room = (RoomScreen) game.getScreen();
            if (joinRoomNotification.isEnter()) {
                room.getRoom().add(joinRoomNotification.getPlayer());
            } else {
                room.getRoom().remove(joinRoomNotification.getPlayer());
            }
            room.updatePlayers();
            room.checkStart();
        }
    }

    @Override
    public void processNotification(NewRoomNotification newRoomNotification) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof LobbyScreen) {
            final LobbyScreen lobby = (LobbyScreen) game.getScreen();
            lobby.roomCreated(newRoomNotification.getRoom());
        }
    }

    @Override
    public void processNotification(ChangePlayerNotification changePlayerNotification) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof RoomScreen) {
            Player player = changePlayerNotification.getPlayer();
            RoomScreen room = (RoomScreen) game.getScreen();
            room.getRoom().getPlayers().remove(player);
            room.getRoom().getPlayers().add(player);
            room.updatePlayers();
            room.checkStart();
        }

    }
}
