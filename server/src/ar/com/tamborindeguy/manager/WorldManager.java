package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.core.WorldServer;
import ar.com.tamborindeguy.database.model.User;
import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import ar.com.tamborindeguy.utils.WorldUtils;
import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import entity.Heading;

import java.util.Collections;
import java.util.Set;

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
                .character()
                .nameText("guidota")
                .clanName("GS Zone")
                .canWrite()
                .networkId(player.getId())
                .aOPhysics();
        E(player).inventory();
        addItem(player.getId(), Type.HELMET);
        addItem(player.getId(), Type.ARMOR);
        addItem(player.getId(), Type.WEAPON);
        addItem(player.getId(), Type.SHIELD);
        addItem(player.getId(), Type.RING);
        ObjectManager.getTypeObjects(Type.POTION).forEach(potion -> {
            E(player).getInventory().add(potion.getId());
        });
        return player;
    }


    private static void addItem(int player, Type type) {
        Set<Obj> objs = ObjectManager.getTypeObjects(type);
        E(player).getInventory().add(objs.iterator().next().getId());
    }

    public static void registerEntity(int connectionId, int id) {
        NetworkComunicator.registerUserConnection(id, connectionId);
        MapManager.addPlayer(id);
    }

    public static void unregisterEntity(int playerToDisconnect) {
        getWorld().delete(playerToDisconnect);
    }

    public static void sendEntityRemove(int user, int playerId) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), new RemoveEntity(playerId));
        }
    }

    public static void sendEntityUpdate(int user, EntityUpdate update) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), update);
        }
    }

    public static void notifyUpdateToNearEntities(EntityUpdate update) {
        MapManager.getNearEntities(update.entityId).forEach(nearPlayer -> {
            sendEntityUpdate(nearPlayer, update);
        });
    }

    public static void notifyUpdate(EntityUpdate update) {
        sendEntityUpdate(update.entityId, update);
        notifyUpdateToNearEntities(update);
    }

    public static void sendCompleteNearEntities(int entityId) {
        MapManager.getNearEntities(entityId).forEach(nearPlayer -> sendEntityUpdate(entityId, new EntityUpdate(nearPlayer, WorldUtils.getComponents(nearPlayer), new Class[0])));
    }

    private static World getWorld() {
        return WorldServer.getWorld();
    }

    public static User getUser(String username) {
        User user = new User();
        return user;
    }

}
