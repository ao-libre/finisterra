package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.core.WorldServer;
import ar.com.tamborindeguy.database.model.User;
import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import ar.com.tamborindeguy.utils.WorldUtils;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import entity.Heading;

import java.util.List;

import static com.artemis.E.E;

public class WorldManager {

    public static Entity createEntity(User user, int connectionId) {
        Entity player = getWorld().createEntity();
        E(player)
                .pos2DX(50)
                .pos2DY(50)
                .expExp(10000)
                .worldPosX(50)
                .worldPosY(50)
                .worldPosMap(1)
                .elvElv(1000)
                .levelLevel(45)
                .headingCurrent(Heading.HEADING_NORTH)
                .headIndex(4)
                .bodyIndex(100)
                .weaponIndex(8)
                .shieldIndex(3)
                .helmetIndex(6)
                .healthMin(120)
                .healthMax(120)
                .hungryMin(100)
                .hungryMax(100)
                .manaMax(1000)
                .manaMin(1000)
                .staminaMin(100)
                .staminaMax(100)
                .thirstMax(100)
                .thirstMin(100)
                .criminal()
                .nameText("guidota")
                .clanName("GS Zone")
                .canWrite()
                .networkId(player.getId())
                .aOPhysics();
        return player;
    }

    public static void registerEntity(int connectionId, int id) {
        NetworkComunicator.registerUserConnection(id, connectionId);
        MapManager.addPlayer(id);
//        notifyUpdateToNearEntities(id, WorldUtils.getComponents(id));
//        sendCompleteNearEntities(id);
    }

    public static void unregisterEntity(int playerToDisconnect) {
        getWorld().delete(playerToDisconnect);
    }

    public static void sendEntityRemove(int user, int playerId) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), new RemoveEntity(playerId));
        }
    }

    public static void sendEntityUpdate(int user, int playerId, List<Component> components) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), new EntityUpdate(playerId, components));
            components.forEach(component -> {
                Log.debug("Sent " + playerId + " " + component + " to " + user);
            });
        }
    }

    public static void notifyUpdateToNearEntities(int entityId, List<Component> components) {
        MapManager.getNearEntities(entityId).forEach(nearPlayer -> {
            sendEntityUpdate(nearPlayer, entityId, components);
        });
    }

    public static void sendCompleteNearEntities(int entityId) {
        MapManager.getNearEntities(entityId).forEach(nearPlayer -> {
            sendEntityUpdate(entityId, nearPlayer, WorldUtils.getComponents(nearPlayer));
        });
    }

    private static World getWorld() {
        return WorldServer.getWorld();
    }

    public static User getUser(String username) {
        User user = new User();
        return user;
    }

}
