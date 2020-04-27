package server.systems.entity.user;

import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.info.Bag;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.item.ItemUsageSystem;
import server.systems.config.ObjectSystem;
import server.systems.network.ServerSystem;
import server.systems.world.WorldEntitiesSystem;
import shared.network.inventory.InventoryUpdate;

@Wire
public class PlayerActionSystem extends PassiveSystem {

    // Injected systems.
    private ServerSystem serverSystem;
    private ItemUsageSystem itemUsageSystem;
    private ObjectSystem objectSystem;
    private WorldEntitiesSystem worldEntitiesSystem;

    public void drop(int connectionId, int count, WorldPos position, int slot) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        E entity = E.E(playerId);

        // Remove item from inventory
        InventoryUpdate update = new InventoryUpdate();
        Bag bag = entity.getBag();
        Bag.Item item = bag.items[slot];
        if (item == null) return;
        if (item.equipped) {
            objectSystem.getObject(item.objId).ifPresent((object) -> {
                itemUsageSystem.TAKE_OFF.accept(playerId, object);
                item.equipped = false;
            });
        }
        item.count -= count;
        if (item.count <= 0) {
            bag.remove(slot);
        }
        update.add(slot, bag.items[slot]); // should remove item if count <= 0
        serverSystem.sendTo(serverSystem.getConnectionByPlayer(playerId), update);

        // Add new obj component.entity to world
        int object = world.create();
        E.E(object).worldPos()
                .worldPosMap(position.map)
                .worldPosX(position.x)
                .worldPosY(position.y);
        E.E(object).objectIndex(item.objId);
        E.E(object).objectCount(count);
        worldEntitiesSystem.registerEntity(object);
    }
}
