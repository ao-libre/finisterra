package server.systems.network;

import com.artemis.BaseSystem;
import server.systems.manager.WorldManager;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityUpdateSystem extends BaseSystem {

    private WorldManager worldManager;

    private final Map<Integer, EntityUpdate> entityUpdates;
    private final Map<Integer, EntityUpdate> publicUpdates;

    public EntityUpdateSystem() {
        entityUpdates = new ConcurrentHashMap<>();
        publicUpdates = new ConcurrentHashMap<>();
    }

    @Override
    protected void processSystem() {
        // send all updates
        entityUpdates.forEach((id, update) -> worldManager.sendEntityUpdate(id, update));
        entityUpdates.clear();

        publicUpdates.forEach((id, update) -> worldManager.notifyToNearEntities(id, update));
        publicUpdates.clear();
    }

    public void add(EntityUpdate update, UpdateTo updateTo) {
        addUpdate(update, updateTo == UpdateTo.ENTITY ? entityUpdates : publicUpdates);
    }

    private void addUpdate(EntityUpdate update, Map<Integer, EntityUpdate> updates) {
        updates.putIfAbsent(update.entityId, update);
        updates.computeIfPresent(update.entityId, (id, otherUpdate) -> EntityUpdateBuilder.join(update, otherUpdate));
    }

    public enum UpdateTo {
        ENTITY,
        NEAR,
    }
}
