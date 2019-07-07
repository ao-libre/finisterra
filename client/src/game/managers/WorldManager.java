package game.managers;

import com.artemis.BaseSystem;

import java.util.*;

import static com.artemis.E.E;

public class WorldManager extends BaseSystem {

    private Map<Integer, Integer> networkedEntities = new HashMap<>();

    public boolean entityExsists(int networkId) {
        return networkedEntities.containsKey(networkId);
    }

    public int getNetworkedEntity(int networkId) {
        return networkedEntities.get(networkId);
    }

    public boolean hasNetworkedEntity(int networkId) {
        return networkedEntities.containsKey(networkId);
    }

    public Set<Integer> getEntities() {
        return new HashSet<>(networkedEntities.values());
    }

    public void registerEntity(int networkId, int entityId) {
        E(entityId).network().getNetwork().id = networkId;
        networkedEntities.put(networkId, entityId);
    }

    public void unregisterEntity(int networkId) {
        int entityId = networkedEntities.get(networkId);
        world.delete(entityId);
        networkedEntities.remove(networkId);
    }

    @Override
    protected void processSystem() {}

    public Optional<Integer> getNetworkedId(int id) {
        return networkedEntities
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == id)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
