package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.core.WorldServer;
import ar.com.tamborindeguy.database.model.User;
import ar.com.tamborindeguy.database.model.attributes.Attributes;
import ar.com.tamborindeguy.database.model.constants.Constants;
import ar.com.tamborindeguy.database.model.modifiers.Modifiers;
import ar.com.tamborindeguy.interfaces.CharClass;
import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import ar.com.tamborindeguy.utils.WorldUtils;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.World;
import com.sun.org.apache.xpath.internal.operations.Mod;
import entity.Heading;

import java.util.Iterator;
import java.util.Set;

import static com.artemis.E.E;

public class WorldManager {

    public static Entity createEntity(String name, int classId, int connectionId) {
        Entity player = getWorld().createEntity();

        E entity = E(player);
        // set position
        setEntityPosition(entity);
        // set head and body
        setHeadAndBody(name, entity);
        // set inventory
        setInventory(player, entity);
        // set class
        setClassAndAttributes(classId, entity);

        return player;
    }

    private static void setClassAndAttributes(int classId, E entity) {
        entity.heroClassClassId(classId);
        CharClass heroClass = CharClass.values()[classId];
        // calculate HP
        calculateHP(heroClass, entity);
        // calculate MANA
        calculateMana(entity, heroClass);
    }

    private static void calculateMana(E entity, CharClass heroClass) {
        float intelligenceAttr = Attributes.INTELLIGENCE.of(heroClass);
        float manaMod = Modifiers.MANA.of(heroClass);
        Float manaBase = Constants.getMana(heroClass);
        int maxMana = (int) (intelligenceAttr * manaMod * manaBase);
        entity.manaMax(maxMana);
        entity.manaMin(maxMana);
    }

    private static void calculateHP(CharClass heroClass, E entity) {
        float heroStrength = Attributes.STRENGTH.of(heroClass);
        float heroHealthMod = Modifiers.HEALTH.of(heroClass);
        Float hpBase = Constants.getHp(heroClass);
        int maxHP = (int) (heroStrength * heroHealthMod * hpBase);
        entity.healthMax(maxHP);
        entity.healthMin(maxHP);
    }

    private static void setInventory(Entity player, E entity) {
        entity.inventory();
        addItem(player.getId(), Type.HELMET);
        addItem(player.getId(), Type.ARMOR);
        addItem(player.getId(), Type.WEAPON);
        addItem(player.getId(), Type.SHIELD);
        addItem(player.getId(), Type.POTION);
        addItem(player.getId(), Type.POTION);
    }

    private static void setHeadAndBody(String name, E entity) {
        entity
                .headingCurrent(Heading.HEADING_NORTH)
                .headIndex(4)
                .bodyIndex(100)
                .character()
                .nameText(name);
    }

    private static void setEntityPosition(E entity) {
        entity
                .worldPosX(50)
                .worldPosY(50)
                .worldPosMap(1);
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
