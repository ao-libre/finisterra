package server.systems.network;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import server.systems.manager.ComponentManager;
import server.systems.manager.WorldManager;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;
import shared.util.EntityUpdateBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import static com.artemis.E.E;
import static shared.network.notifications.EntityUpdate.NO_ENTITY;

@Wire
public class EntityUpdateSystem extends BaseSystem {

    private WorldManager worldManager;
    private ComponentManager componentManager;

    private final Map<Integer, Deque<EntityUpdate>> entityUpdates;
    private final Map<Integer, Deque<EntityUpdate>> publicUpdates;

    public EntityUpdateSystem() {
        entityUpdates = new ConcurrentHashMap<>();
        publicUpdates = new ConcurrentHashMap<>();
    }

    @Override
    protected void processSystem() {
        // send all updates
        entityUpdates.forEach((id, update) -> {
            worldManager.sendEntityUpdate(id, update.toArray(new EntityUpdate[0]));
        });
        entityUpdates.clear();

        publicUpdates.forEach((id, update) -> {
            Log.debug("Notifying near to: " + id);
            worldManager.notifyToNearEntities(id, update.toArray(new EntityUpdate[0]));
            Log.debug("Notifications ended for: " + id);
        });
        publicUpdates.clear();
    }

    public void add(EntityUpdate update, UpdateTo updateTo) {
        add(update.entityId, update, updateTo);
    }

    public void add(int entity, EntityUpdate update, UpdateTo updateTo) {
        Log.debug("Will add update: " + update.toString() + " " + updateTo.name());
        // search all updates of this update component.entity and remove them
        if (update instanceof RemoveEntity) {
            if (entityUpdates.containsKey(entity)) {
                entityUpdates.get(entity).removeIf(entityUpdate -> entityUpdate.entityId == update.entityId);
            }
            if (publicUpdates.containsKey(entity)) {
                publicUpdates.get(entity).removeIf(entityUpdate -> entityUpdate.entityId == update.entityId);
            }
        }

        switch (updateTo) {
            case ALL:
                addUpdate(entity, update, entityUpdates);
                addUpdate(entity, update, publicUpdates);
                break;
            case NEAR:
                addUpdate(entity, update, publicUpdates);
                break;
            case ENTITY:
                addUpdate(entity, update, entityUpdates);
                break;
        }
    }

    private void addUpdate(int entity, EntityUpdate update, Map<Integer, Deque<EntityUpdate>> updates) {
        updates.putIfAbsent(entity, new ConcurrentLinkedDeque<>());
        updates.computeIfPresent(entity, (id, idUpdates) -> {
            // find updates for same entity and merge update to avoid multiple packets
            Set<EntityUpdate> toMerge = idUpdates
                    .stream()
                    .filter(u -> u.entityId != NO_ENTITY)
                    .filter(u -> u.entityId == update.entityId)
                    .collect(Collectors.toSet());
            toMerge.add(update);
            if (toMerge.size() > 1) {
                Log.debug("Updates to be merged: ");
                toMerge.forEach(it -> Log.debug(" - " + it.toString()));
                EntityUpdate mergedUpdate = EntityUpdateBuilder.merge(toMerge);
                toMerge.forEach(idUpdates::remove);
                idUpdates.add(mergedUpdate);
                Log.debug("Update merged: " + mergedUpdate.toString());
            } else {
                idUpdates.add(update);
            }
            return idUpdates;
        });
    }

    // Attach entity to another entity and send update to all near entities including component.entity
    public void attach(int entity, int entityToAttach) {
        E(entityToAttach).refId(entity);
        List<Component> components = componentManager.getComponents(entityToAttach, ComponentManager.Visibility.CLIENT_PUBLIC);
        EntityUpdate update = EntityUpdateBuilder.of(entityToAttach).withComponents(components).build();
        add(entity, update, UpdateTo.ALL);
    }

    public void detach(int entity, Integer sEntity) {
        add(entity, EntityUpdateBuilder.delete(sEntity), UpdateTo.ALL);
        world.delete(sEntity);
    }
}
