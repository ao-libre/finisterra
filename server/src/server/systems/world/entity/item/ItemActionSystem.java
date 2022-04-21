package server.systems.world.entity.item;

import com.artemis.ComponentMapper;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.entity.character.info.Bag;
import component.entity.world.Object;
import component.physics.UseInterval;
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

    ComponentMapper<Bag> mBag;
    ComponentMapper<Object> mObject;
    ComponentMapper<UseInterval> mUseInterval;
    ComponentMapper<WorldPos> mWorldPos;

    public void useItem(int playerId, int action, int slot) {
        Bag bag = mBag.get(playerId);
        Bag.Item[] userItems = bag.getItems();
        if (slot < userItems.length) {
            // if item isequipable...
            Bag.Item item = userItems[slot];
            if (item == null) return;
            if (action == ItemActionRequest.ItemAction.EQUIP.ordinal() && itemSystem.isEquippable(item)) {
                // modify user equipment
                itemSystem.equip(playerId, slot, item);
            } else if (action == ItemActionRequest.ItemAction.USE.ordinal() && itemSystem.isUsable(item)) {
                if (!mUseInterval.has(playerId)) {
                    itemSystem.use(playerId, item);
                    UseInterval useInterval = mUseInterval.create(playerId);
                    useInterval.setValue(Intervals.USE_INTERVAL);
                } else {
                    messageSystem.add(playerId,
                            ConsoleMessage.error(Messages.CANT_USE_THAT_FAST.name()));
                }
            }
        }
    }

    public void grabItem(int playerId) {
        WorldPos playerPos = mWorldPos.get(playerId);
        mapSystem.getNearEntities(playerId)
                .stream()
                .filter(entityId -> {
                    WorldPos entityPos = mWorldPos.get(entityId);
                    return mObject.has(entityId) && entityPos.x == playerPos.x && entityPos.y == playerPos.y;
                })
                .findFirst()
                .ifPresent(objectEntityId -> {
                    Object object = mObject.get(objectEntityId);
                    Bag bag = mBag.get(playerId);
                    int index = bag.add(object.index, object.count, false);
                    if (index >= 0) {
                        Log.info("Adding item to index: " + index);
                        InventoryUpdate update = new InventoryUpdate();
                        update.add(index, bag.getItems()[index]);
                        serverSystem.sendByPlayerId(playerId, update);
                        worldEntitiesSystem.unregisterEntity(objectEntityId);
                    } else {
                        Log.info("Could not put item in inventory (FULL?)");
                    }
                });
    }
}
