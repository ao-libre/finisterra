package server.systems.user;

import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.entity.character.info.Bag;
import component.entity.world.Object;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.ServerSystem;
import server.systems.manager.ItemManager;
import server.systems.manager.MapManager;
import server.systems.manager.WorldManager;
import server.systems.network.MessageSystem;
import shared.interfaces.Intervals;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.util.Messages;

public class ItemActionSystem extends PassiveSystem {

    // Injected systems.
    private ServerSystem serverSystem;
    private ItemManager itemManager;
    private MessageSystem messageSystem;
    private MapManager mapManager;
    private WorldManager worldManager;

    public void useItem(int connectionId, int action, int slot) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        E player = E.E(playerId);
        Bag.Item[] userItems = player.bagItems();
        if (slot < userItems.length) {
            // if item isequipable...
            Bag.Item item = userItems[slot];
            if (item == null) return;
            if (action == ItemActionRequest.ItemAction.EQUIP.ordinal() && itemManager.isEquippable(item)) {
                // modify user equipment
                itemManager.equip(playerId, slot, item);
            } else if (action == ItemActionRequest.ItemAction.USE.ordinal() && itemManager.isUsable(item)) {
                if (!player.hasUseInterval()) {
                    itemManager.use(playerId, item);
                    player.useIntervalValue(Intervals.USE_INTERVAL);
                } else {
                    messageSystem.add(playerId,
                            ConsoleMessage.error(Messages.CANT_USE_THAT_FAST.name()));
                }
            }
        }
    }

    public void grabItem(int connectionId) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        E player = E.E(playerId);
        WorldPos playerPos = player.getWorldPos();
        mapManager.getNearEntities(playerId)
                .stream()
                .filter(entityId -> {
                    WorldPos entityPos = E.E(entityId).getWorldPos();
                    return E.E(entityId).hasObject() && entityPos.x == playerPos.x && entityPos.y == playerPos.y;
                })
                .findFirst()
                .ifPresent(objectEntityId -> {
                    Object object = E.E(objectEntityId).getObject();
                    int index = player.getBag().add(object.index, object.count, false);
                    if (index >= 0) {
                        Log.info("Adding item to index: " + index);
                        InventoryUpdate update = new InventoryUpdate();
                        update.add(index, player.bagItems()[index]);
                        serverSystem.sendTo(connectionId, update);
                        worldManager.unregisterEntity(objectEntityId);
                    } else {
                        Log.info("Could not put item in inventory (FULL?)");
                    }
                });
    }
}
