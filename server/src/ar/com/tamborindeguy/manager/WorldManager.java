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
import entity.Heading;

import java.util.Iterator;
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
                .manaMax(9999)
                .manaMin(9999)
                .staminaMin(9999)
                .staminaMax(9999)
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
        addItem(player.getId(), Type.POTION);
        addItem(player.getId(), Type.POTION);
        return player;
    }

    private static void addItem(int player, Type type) {
        Set<Obj> objs = ObjectManager.getTypeObjects(type);
        Iterator<Obj> iterator = objs.iterator();
        E(player).getInventory().add(iterator.next().getId());
    }

    public static void registerItem(int id){
        MapManager.addItem(id);
    }

    public static void registerEntity(int connectionId, int id) {
        NetworkComunicator.registerUserConnection(id, connectionId);
        MapManager.addPlayer(id);
    }

    public static void unregisterEntity(int playerToDisconnect) {
        getWorld().delete(playerToDisconnect);
    }

    static void sendEntityRemove(int user, int entity) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), new RemoveEntity(entity));
        }
    }

    static void sendEntityUpdate(int user, Object update) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), update);
        }
    }

    public static void notifyToNearEntities(int entityId, Object update) {
        MapManager.getNearEntities(entityId).forEach(nearPlayer -> {
            sendEntityUpdate(nearPlayer, update);
        });
    }

    public static void notifyUpdate(int entityId, Object update) {
        sendEntityUpdate(entityId, update);
        notifyToNearEntities(entityId, update);
    }

    public static void sendCompleteNearEntities(int entityId) {
        MapManager.getNearEntities(entityId).forEach(nearEntity -> sendEntityUpdate(entityId, new EntityUpdate(nearEntity, WorldUtils.getComponents(nearEntity), new Class[0])));
    }

    private static World getWorld() {
        return WorldServer.getWorld();
    }

    public static User getUser(String username) {
        User user = new User();
        return user;
    }

}
