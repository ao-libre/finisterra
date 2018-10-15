package ar.com.tamborindeguy.network;

import ar.com.tamborindeguy.database.model.User;
import ar.com.tamborindeguy.interfaces.Constants;
import ar.com.tamborindeguy.manager.CombatManager;
import ar.com.tamborindeguy.manager.ItemManager;
import ar.com.tamborindeguy.manager.MapManager;
import ar.com.tamborindeguy.manager.WorldManager;
import ar.com.tamborindeguy.network.combat.AttackRequest;
import ar.com.tamborindeguy.network.interaction.MeditateRequest;
import ar.com.tamborindeguy.network.interaction.TakeItemRequest;
import ar.com.tamborindeguy.network.interaction.TalkRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;
import ar.com.tamborindeguy.network.interfaces.IResponse;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.inventory.ItemActionRequest;
import ar.com.tamborindeguy.network.login.LoginFailed;
import ar.com.tamborindeguy.network.login.LoginOK;
import ar.com.tamborindeguy.network.login.LoginRequest;
import ar.com.tamborindeguy.network.movement.MovementRequest;
import ar.com.tamborindeguy.network.movement.MovementResponse;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.util.MapUtils;
import ar.com.tamborindeguy.utils.WorldUtils;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.Entity;
import entity.Heading;
import entity.Object;
import entity.character.info.Inventory;
import entity.character.states.Meditating;
import graphics.FX;
import physics.AttackAnimation;
import position.WorldPos;

import java.util.Optional;

import static com.artemis.E.E;

public class ServerRequestProcessor implements IRequestProcessor {

    @Override
    public void processRequest(LoginRequest request, int connectionId) {
        IResponse response;
        User user = WorldManager.getUser(request.username);
        if (user == null) {
            response = new LoginFailed("User doesn't exists");
//        } else if (user.getPassword().equals(request.password)) {
        } else {
            final Entity entity = WorldManager.createEntity(user, connectionId);
            int map = E(entity).getWorldPos().map;
            NetworkComunicator.sendTo(connectionId, new EntityUpdate(entity.getId(), WorldUtils.getComponents(entity.getId()), new Class[0]));
            NetworkComunicator.sendTo(connectionId, new LoginOK(entity.getId()));
            WorldManager.registerEntity(connectionId, entity.getId());
//        } else {
//            response = new LoginFailed("Wrong password");
        }
//        NetworkComunicator.sendTo(connectionId, response);
    }

    @Override
    public void processRequest(MovementRequest request, int connectionId) {
        // TODO check map changed

        // validate if valid
        int playerId = NetworkComunicator.getPlayerByConnection(connectionId);

        // update server entity
        E player = E(playerId);

        player.headingCurrent(WorldUtils.getHeading(request.movement));

        WorldPos worldPos = player.getWorldPos();
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = WorldUtils.getNextPos(worldPos, request.movement);
        boolean blocked = MapUtils.isBlocked(MapManager.get(nextPos.map), nextPos);
        boolean occupied = MapUtils.hasEntity(MapManager.getNearEntities(playerId), nextPos);
        if (player.hasImmobile() || blocked || occupied) {
            nextPos = worldPos;
        }

        player.worldPosMap(nextPos.map);
        player.worldPosX(nextPos.x);
        player.worldPosY(nextPos.y);
        player.destinationDir(request.movement);
        player.destinationWorldPos(player.getWorldPos());

        MapManager.movePlayer(playerId, Optional.of(oldPos));

        // notify near users
        WorldManager.notifyUpdateToNearEntities(new EntityUpdate(playerId, new Component[] {player.getHeading(), player.getDestination()}, new Class[0]));

        // notify user
        NetworkComunicator.sendTo(connectionId, new MovementResponse(request.requestNumber, nextPos));
    }

    @Override
    public void processRequest(AttackRequest attackRequest, int connectionId) {
        int playerId = NetworkComunicator.getPlayerByConnection(connectionId);
        E player = E(playerId);

        WorldPos worldPos = player.getWorldPos();
        Heading heading = player.getHeading();
        WorldPos facingPos = WorldUtils.getFacingPos(worldPos, heading);

        Optional<Integer> victim = MapManager.getNearEntities(playerId)
                .stream()
                .filter(near -> E(near).hasWorldPos() && E(near).getWorldPos().equals(facingPos))
                .findFirst();
        if (victim.isPresent()) {
            Optional<Integer> damage = CombatManager.attack(playerId, victim.get());
            if (damage.isPresent()) {
                CombatManager.notify(victim.get(), "-" + Integer.toString(damage.get()));
            } else {
                CombatManager.notify(playerId, CombatManager.MISS);
            }
        } else {
            CombatManager.notify(playerId, CombatManager.MISS);
        }
        WorldManager.notifyUpdate(new EntityUpdate(playerId, new Component[]{new AttackAnimation()}, new Class[0]));
    }

    @Override
    public void processRequest(ItemActionRequest itemAction, int connectionId) {
        int playerId = NetworkComunicator.getPlayerByConnection(connectionId);
        E player = E(playerId);
        Inventory.Item[] userItems = player.getInventory().items;
        int itemIndex = itemAction.getSlot();
        if (itemIndex < userItems.length) {
            // if equipable
            Inventory.Item item = userItems[itemIndex];
            if (item == null) {
                return;
            }
            if (ItemManager.isEquippable(item)) {
                // modify user equipment
                item.equipped = !item.equipped;
                ItemManager.equip(playerId, itemIndex, item);
            } else if (ItemManager.isUsable(item)) {
                ItemManager.use(playerId, itemIndex, item);
            }
        }
    }

    @Override
    public void processRequest(MeditateRequest meditateRequest, int connectionId) {
        int playerId = NetworkComunicator.getPlayerByConnection(connectionId);
        E player = E(playerId);
        boolean meditating = player.isMeditating();
        if (meditating) {
            player.removeFX();
            player.removeMeditating();
            WorldManager.notifyUpdate(new EntityUpdate(player.networkId(), new Component[0], new Class[]{FX.class, Meditating.class}));
        } else {
            player.fXAddParticleEffect(Constants.MEDITATE_NW_FX);
            player.meditating();
            WorldManager.notifyUpdate(new EntityUpdate(player.networkId(), new Component[] {player.getMeditating(), player.getFX()}, new Class[0]));
        }
    }

    @Override
    public void processRequest(TalkRequest talkRequest, int connectionId) {
        int playerId = NetworkComunicator.getPlayerByConnection(connectionId);
        E player = E(playerId);
        player.dialogText(talkRequest.getMessage());
        WorldManager.notifyUpdate(new EntityUpdate(player.networkId(), new Component[]{player.getDialog()}, new Class[0]));
    }

    @Override
    public void processRequest(TakeItemRequest takeItemRequest, int connectionId) {
        int playerId = NetworkComunicator.getPlayerByConnection(connectionId);
        E player = E(playerId);
        WorldPos playerPos = player.getWorldPos();
        MapManager.getNearEntities(playerId)
                .stream()
                .filter(entityId -> {
                    WorldPos entityPos = E(entityId).getWorldPos();
                    return E(entityId).hasObject() && entityPos.x == playerPos.x && entityPos.y == playerPos.y;
                }).findFirst()
        .ifPresent(objectEntityId -> {
            Object object = E(objectEntityId).getObject();
            int index = player.getInventory().add(object.index, object.count);
            if (index >= 0) {
                InventoryUpdate update = new InventoryUpdate();
                update.add(index, player.getInventory().items[index]);
                NetworkComunicator.sendTo(playerId, update);
                WorldManager.unregisterEntity(objectEntityId);
                MapManager.removeEntity(objectEntityId);
            }
        });
    }

}
