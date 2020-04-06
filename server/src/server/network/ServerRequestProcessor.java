package server.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import component.entity.character.info.Bag;
import component.entity.world.Dialog;
import component.entity.world.Object;
import component.movement.Destination;
import component.physics.AOPhysics;
import component.position.WorldPos;
import server.systems.CommandSystem;
import server.systems.MeditateSystem;
import server.systems.ServerSystem;
import server.systems.combat.MagicCombatSystem;
import server.systems.combat.PhysicalCombatSystem;
import server.systems.combat.RangedCombatSystem;
import server.systems.manager.*;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.UpdateTo;
import server.utils.WorldUtils;
import shared.model.AttackType;
import shared.model.lobby.Player;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.DropItem;
import shared.network.interaction.MeditateRequest;
import shared.network.interaction.TakeItemRequest;
import shared.network.interaction.TalkRequest;
import shared.network.interfaces.DefaultRequestProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.network.inventory.ItemActionRequest.ItemAction;
import shared.network.lobby.player.PlayerLoginRequest;
import shared.network.movement.MovementNotification;
import shared.network.movement.MovementRequest;
import shared.network.movement.MovementResponse;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;
import shared.network.time.TimeSyncRequest;
import shared.network.time.TimeSyncResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

/**
 * Every packet received from users will be processed here
 */
@Wire
public class ServerRequestProcessor extends DefaultRequestProcessor {

    // Injected Systems
    private ServerSystem networkManager;
    private MapManager mapManager;
    private WorldManager worldManager;
    private PhysicalCombatSystem physicalCombatSystem;
    private MagicCombatSystem magicCombatSystem;
    private ItemManager itemManager;
    private SpellManager spellManager;
    private MeditateSystem meditateSystem;
    private RangedCombatSystem rangedCombatSystem;
    private CommandSystem commandSystem;
    private ObjectManager objectManager;
    private EntityUpdateSystem entityUpdateSystem;

    private List<WorldPos> getArea(WorldPos worldPos, int range /*impar*/) {
        List<WorldPos> positions = new ArrayList<>();
        int i = range / 2;
        for (int x = worldPos.x - i; x <= worldPos.x + i; x++) {
            for (int y = worldPos.y - i; y <= worldPos.y + i; y++) {
                positions.add(new WorldPos(x, y, worldPos.map));
            }
        }
        return positions;
    }

    @Override
    public void processRequest(PlayerLoginRequest playerLoginRequest, int connectionId) {
        Player player = playerLoginRequest.getPlayer();
        worldManager.login(connectionId, player);
    }

    /**
     * Process {@link MovementRequest}. If it is valid, move player and notify.
     *
     * @param request      component.movement request
     * @param connectionId id
     * @see MovementRequest
     */
    @Override
    public void processRequest(MovementRequest request, int connectionId) {
        if (networkManager.connectionHasNoPlayer(connectionId)) {
            return;
        }
        int playerId = networkManager.getPlayerByConnection(connectionId);
        E player = E(playerId);
        WorldUtils worldUtils = WorldUtils(world);
        AOPhysics.Movement movement = AOPhysics.Movement.values()[request.movement];
        player.headingCurrent(worldUtils.getHeading(movement));

        WorldPos worldPos = player.getWorldPos();
        WorldPos oldPos = new WorldPos(worldPos);
        WorldPos nextPos = worldUtils.getNextPos(worldPos, movement);
        Map map = mapManager.getMap(nextPos.map);
        boolean blocked = mapManager.getHelper().isBlocked(map, nextPos);
        boolean occupied = mapManager.getHelper().hasEntity(mapManager.getNearEntities(playerId), nextPos);
        if (!(player.hasImmobile() || blocked || occupied)) {
            Tile tile = mapManager.getMap(nextPos.map).getTile(nextPos.x, nextPos.y);
            WorldPosition tileExit = tile.getTileExit();
            if (tileExit != null) {
                Log.info("Moving to exit tile: " + tileExit);
                nextPos = new WorldPos(tileExit.getX(), tileExit.getY(), tileExit.getMap());
            }
            player
                    .worldPosMap(nextPos.map)
                    .worldPosX(nextPos.x)
                    .worldPosY(nextPos.y);
        } else {
            nextPos = oldPos;
        }

        mapManager.movePlayer(playerId, Optional.of(oldPos));

        // notify near users
        if (!nextPos.equals(oldPos)) {
            if (nextPos.map != oldPos.map) {
                entityUpdateSystem.add(EntityUpdateBuilder.of(playerId).withComponents(E(playerId).getWorldPos()).build(), UpdateTo.NEAR);
            } else {
                // TODO convert notification into component.entity update
                worldManager.notifyToNearEntities(playerId, new MovementNotification(playerId, new Destination(nextPos, request.movement)));
            }
        } else {
            entityUpdateSystem.add(EntityUpdateBuilder.of(playerId).withComponents(player.getHeading()).build(), UpdateTo.NEAR);
        }

        // notify user
        networkManager.sendTo(connectionId, new MovementResponse(request.requestNumber, nextPos));
    }

    /**
     * Attack and notify, if it was effective or not, to near users
     *
     * @param attackRequest attack type
     * @param connectionId  user connection id
     */
    @Override
    public void processRequest(AttackRequest attackRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        AttackType type = attackRequest.type();
        if (type.equals(AttackType.RANGED)) {
            rangedCombatSystem.shoot(playerId, attackRequest);
        } else {
            physicalCombatSystem.entityAttack(playerId, Optional.empty());
        }
    }

    /**
     * User wants to use or act over an item, do action and notify.
     *
     * @param itemAction   user slot number
     * @param connectionId user connection id
     */
    @Override
    public void processRequest(ItemActionRequest itemAction, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        E player = E(playerId);
        Bag.Item[] userItems = player.bagItems();
        int itemIndex = itemAction.getSlot();
        if (itemIndex < userItems.length) {
            // if equipable
            Bag.Item item = userItems[itemIndex];
            if (item == null) {
                return;
            }
            if (itemAction.getAction() == ItemAction.EQUIP.ordinal() && itemManager.isEquippable(item)) {
                // modify user equipment
                itemManager.equip(playerId, itemIndex, item);
            } else if (itemAction.getAction() == ItemAction.USE.ordinal() && itemManager.isUsable(item)) {
                itemManager.use(playerId, item);
            }
        }
    }

    /**
     * User wants to meditate
     *
     * @param meditateRequest request (no data)
     * @param connectionId    user connection id
     */
    @Override
    public void processRequest(MeditateRequest meditateRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        meditateSystem.toggle(playerId);
    }

    /**
     * If the talk request starts with '/' it means is a command
     * If not, notify near users that user talked
     *
     * @param talkRequest  talk request with message
     * @param connectionId user connection id
     */
    @Override
    public void processRequest(TalkRequest talkRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);

        if (talkRequest.getMessage().startsWith("/")) {
            commandSystem.handleCommand(talkRequest.getMessage(), playerId);
        } else {
            EntityUpdate update = EntityUpdateBuilder.of(playerId).withComponents(new Dialog(talkRequest.getMessage())).build();
            entityUpdateSystem.add(update, UpdateTo.ALL);
        }
    }

    /**
     * User wants to take something from ground
     *
     * @param takeItemRequest request (no data)
     * @param connectionId    user connection id
     */
    @Override
    public void processRequest(TakeItemRequest takeItemRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        E player = E(playerId);
        WorldPos playerPos = player.getWorldPos();
        mapManager.getNearEntities(playerId)
                .stream()
                .filter(entityId -> {
                    WorldPos entityPos = E(entityId).getWorldPos();
                    return E(entityId).hasObject() && entityPos.x == playerPos.x && entityPos.y == playerPos.y;
                })
                .findFirst()
                .ifPresent(objectEntityId -> {
                    Object object = E(objectEntityId).getObject();
                    int index = player.getBag().add(object.index, object.count, false);
                    if (index >= 0) {
                        Log.info("Adding item to index: " + index);
                        InventoryUpdate update = new InventoryUpdate();
                        update.add(index, player.bagItems()[index]);
                        networkManager.sendTo(connectionId, update);
                        worldManager.unregisterEntity(objectEntityId);
                    } else {
                        Log.info("Could not put item in inventory (FULL?)");
                    }
                });
    }

    @Override
    public void processRequest(SpellCastRequest spellCastRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        magicCombatSystem.spell(playerId, spellCastRequest);
    }

    @Override
    public void processRequest(TimeSyncRequest request, int connectionId) {
        long receiveTime = TimeUtils.millis();
        TimeSyncResponse response = new TimeSyncResponse();
        response.receiveTime = receiveTime;
        response.requestId = request.requestId;
        response.sendTime = TimeUtils.millis();
        networkManager.sendTo(connectionId, response);
    }

    @Override
    public void processRequest(DropItem dropItem, int connectionId) {
        int slot = dropItem.getSlot();
        int playerId = networkManager.getPlayerByConnection(connectionId);
        E entity = E(playerId);
        // remove item from inventory
        InventoryUpdate update = new InventoryUpdate();
        Bag bag = entity.getBag();
        Bag.Item item = bag.items[slot];
        if (item == null) {
            return;
        }
        if (item.equipped) {
            itemManager.getItemConsumers().TAKE_OFF.accept(playerId, objectManager.getObject(item.objId).get());
            item.equipped = false;
        }
        item.count -= dropItem.getCount();
        if (item.count <= 0) {
            bag.remove(slot);
        }
        update.add(slot, bag.items[slot]); // should remove item if count <= 0
        networkManager.sendTo(networkManager.getConnectionByPlayer(playerId), update);
        // add new obj component.entity to world
        int object = world.create();
        E(object).worldPos()
                .worldPosMap(dropItem.getPosition().map)
                .worldPosX(dropItem.getPosition().x)
                .worldPosY(dropItem.getPosition().y);
        E(object).objectIndex(item.objId);
        E(object).objectCount(dropItem.getCount());
        worldManager.registerEntity(object);
    }
}
