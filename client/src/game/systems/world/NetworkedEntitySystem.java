package game.systems.world;

import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import java.util.*;

import static com.artemis.E.E;

public class NetworkedEntitySystem extends PassiveSystem {

    private final Map<Integer, Integer> networkedEntities = new HashMap<>();

    public void registerEntity(int networkId, int entityId) {
        E(entityId).networkId(networkId);
        networkedEntities.put(networkId, entityId);
    }

    public void unregisterEntity(int networkId) {
        try {
            int entityId = networkedEntities.get(networkId);
            world.delete(entityId);
            networkedEntities.remove(networkId);
        } catch (Exception e) {
            Log.error("Couldn't remove entity: " + networkId, e);
        }
    }

    public boolean exists(int networkId) {
        return networkedEntities.containsKey(networkId);
    }

    public int get(int networkId) {
        return networkedEntities.get(networkId);
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
