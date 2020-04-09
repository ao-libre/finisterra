package game.systems.world;

import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

public class NetworkedEntitySystem extends PassiveSystem {

    private final Map<Integer, Integer> networkedEntities;
    private final Map<Integer, Integer> localEntities;

    public NetworkedEntitySystem() {
        localEntities = new ConcurrentHashMap<>();
        networkedEntities = new ConcurrentHashMap<>();
    }

    public void registerEntity(int networkId, int entityId) {
        E(entityId).networkId(networkId);
        networkedEntities.put(networkId, entityId);
        localEntities.put(entityId, networkId);
    }

    public void unregisterEntity(int networkId) {
        try {
            int entityId = networkedEntities.get(networkId);
            localEntities.remove(entityId);
            world.delete(entityId);
            networkedEntities.remove(networkId);
        } catch (Exception e) {
            Log.error("Couldn't remove entity: " + networkId, e);
        }
    }

    public void unregisterLocalEntity(int entityId) {
        Integer networkId = localEntities.get(entityId);
        networkedEntities.remove(networkId);
        localEntities.remove(entityId);
        world.delete(entityId);
    }

    public boolean exists(int networkId) {
        return networkedEntities.containsKey(networkId);
    }

    public int getLocalId(int networkId) {
        return networkedEntities.get(networkId);
    }

    public boolean existsLocal(int localId) {
        return networkedEntities.containsKey(localId);
    }

    public int getNetworkId(int localId) {
        return localEntities.get(localId);
    }

    public Set<Integer> getAll() {
        return new HashSet<>(networkedEntities.values());
    }

    public Optional<Integer> getNetworkedId(int id) {
        return networkedEntities
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == id)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
