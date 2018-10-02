package ar.com.tamborindeguy.client.managers;

import ar.com.tamborindeguy.client.screens.GameScreen;
import com.artemis.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldManager {

    public static Map<Integer, Integer> networkedEntities = new HashMap<>();

    public static boolean entityExsists(int networkId) {
        return networkedEntities.containsKey(networkId);
    }

    public static int getNetworkedEntity(int networkId) {
        return networkedEntities.get(networkId);
    }

    public static Set<Integer> getEntities() {
        return new HashSet<>(networkedEntities.values());
    }

    public static void registerEntity(int networkId, int entityId) {
        networkedEntities.put(networkId, entityId);
    }

    public static void unregisterEntity(int networkId) {
        int entityId = networkedEntities.get(networkId);
        getWorld().delete(entityId);
        networkedEntities.remove(networkId);
    }

    public static World getWorld() {
        return GameScreen.getWorld();
    }
}
