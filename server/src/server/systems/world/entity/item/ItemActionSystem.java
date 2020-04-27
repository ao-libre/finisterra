package server.systems.world.entity.item;

import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.entity.character.info.Bag;
import component.entity.world.Object;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.MessageSystem;
import server.systems.network.ServerSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import shared.interfaces.Intervals;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.util.Messages;

public class ItemActionSystem extends PassiveSystem {

    // Injected systems.
    private ServerSystem serverSystem;
    private ItemSystem itemSystem;
    private MessageSystem messageSystem;
    private MapSystem mapSystem;
    private WorldEntitiesSystem worldEntitiesSystem;

    public void useItem(int connectionId, int action, int slot) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        E player = E.E(playerId);
        Bag.Item[] userItems = player.bagItems();
        if (slot < userItems.length) {
            // if item isequipable...
            Bag.Item item = userItems[slot];
            if (item == null) return;
            if (action == ItemActionRequest.ItemAction.EQUIP.ordinal() && itemSystem.isEquippable(item)) {
                // modify user equipment
                itemSystem.equip(playerId, slot, item);
            } else if (action == ItemActionRequest.ItemAction.USE.ordinal() && itemSystem.isUsable(item)) {
                if (!player.hasUseInterval()) {
                    itemSystem.use(playerId, item);
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
        mapSystem.getNearEntities(playerId)
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
                        worldEntitiesSystem.unregisterEntity(objectEntityId);
                    } else {
                        Log.info("Could not put item in inventory (FULL?)");
                    }
                });
    }
}
