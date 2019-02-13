package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.core.WorldServer;
import ar.com.tamborindeguy.database.model.attributes.Attributes;
import ar.com.tamborindeguy.database.model.constants.Constants;
import ar.com.tamborindeguy.database.model.modifiers.Modifiers;
import ar.com.tamborindeguy.interfaces.CharClass;
import ar.com.tamborindeguy.interfaces.Hero;
import ar.com.tamborindeguy.interfaces.Race;
import ar.com.tamborindeguy.network.NetworkComunicator;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import ar.com.tamborindeguy.objects.types.*;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import entity.Heading;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

public class WorldManager {

    public static Entity createEntity(String name, int heroId) {
        Entity player = getWorld().createEntity();

        E entity = E(player);
        // set position
        setEntityPosition(entity);
        // set head and body
        setHeadAndBody(name, entity);
        // set class
        setClassAndAttributes(heroId, entity);
        // set inventory
        setInventory(player, entity);

        return player;
    }

    private static void setClassAndAttributes(int heroId, E entity) {
        Hero hero = Hero.values()[heroId];
        entity.charHeroHeroId(heroId);
        CharClass charClass = CharClass.values()[hero.getClassId()];
        // calculate HP
        calculateHP(charClass, entity);
        // calculate MANA
        calculateMana(entity, charClass);
        // set stamina
        entity.staminaMax(100);
        entity.staminaMin(100);
        // set body and head
        Race race = Race.values()[hero.getRaceId()];
        setNakedBody(entity, race);
        setHead(entity, race);
    }

    private static void setHead(E entity, Race race) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int headIndex = 0;
        switch (race) {
            case HUMAN:
                headIndex = random.nextInt(1, 51 + 1);
                break;
            case DROW:
                headIndex = random.nextInt(201, 221 + 1);
                break;
            case ELF:
                headIndex = random.nextInt(101, 122 + 1);
                break;
            case GNOME:
                headIndex = random.nextInt(401, 416 + 1);
                break;
            case DWARF:
                headIndex = random.nextInt(301, 319 + 1);
        }
        entity.headIndex(headIndex);
    }

    public static void setNakedBody(E entity, Race race) {
        int bodyIndex = 1;
        switch (race) {
            case GNOME:
                bodyIndex = 222;
                break;
            case DWARF:
                bodyIndex = 53;
                break;
            case ELF:
                bodyIndex = 210;
                break;
            case DROW:
                bodyIndex = 32;
                break;
            case HUMAN:
                bodyIndex = 21;
        }
        entity.bodyIndex(bodyIndex);
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
        addPotion(player.getId(), PotionKind.HP);
        addPotion(player.getId(), PotionKind.MANA);
    }

    private static void setHeadAndBody(String name, E entity) {
        entity
                .headingCurrent(Heading.HEADING_NORTH)
                .character()
                .nameText(name);
    }

    private static void setEntityPosition(E entity) {
        entity
                .worldPosX(50)
                .worldPosY(50)
                .worldPosMap(1);
    }

    private static void addPotion(int player, PotionKind kind) {
        Set<Obj> objs = ObjectManager.getTypeObjects(Type.POTION);
        objs.stream() //
                .map(PotionObj.class::cast) //
                .filter(potion -> {
                    PotionKind potionKind = potion.getKind();
                    return potionKind != null && potionKind.equals(kind);
                }) //
                .findFirst() //
                .ifPresent(obj -> E(player).getInventory().add(obj.getId()));
    }

    private static void addItem(int player, Type type) {
        Set<Obj> objs = ObjectManager.getTypeObjects(type);
        objs.stream()
                .filter(obj -> {
                    if (obj instanceof ObjWithClasses) {
                        int heroId = E(player).getCharHero().heroId;
                        Hero hero = Hero.values()[heroId];
                        CharClass clazz = CharClass.values()[hero.getClassId()];
                        Set<CharClass> forbiddenClasses = ((ObjWithClasses) obj).getForbiddenClasses();
                        boolean supported = forbiddenClasses.size() == 0 || !forbiddenClasses.contains(clazz);
                        if (supported && obj instanceof ArmorObj) {
                            Race race = Race.values()[hero.getRaceId()];
                            if (race.equals(Race.GNOME) || race.equals(Race.DWARF)) {
                                supported = ((ArmorObj) obj).isDwarf();
                            } else if (((ArmorObj) obj).isWomen()) {
                                supported = false; // TODO
                            }
                        }
                        return supported;
                    } else if (obj.getType().equals(Type.POTION)) {
                        PotionObj potion = (PotionObj) obj;
                        return potion.getKind().equals(PotionKind.HP) || potion.getKind().equals(PotionKind.MANA);
                    }
                    return true;
                })
                .findFirst()
                .ifPresent(obj -> E(player).getInventory().add(obj.getId()));
    }

    public static void registerItem(int id) {
        MapManager.updateEntity(id);
    }

    public static void registerEntity(int connectionId, int id) {
        NetworkComunicator.registerUserConnection(id, connectionId);
        MapManager.updateEntity(id);
    }

    public static void unregisterEntity(int playerToDisconnect) {
        getWorld().delete(playerToDisconnect);
    }

    static void sendEntityRemove(int user, int entity) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), new RemoveEntity(entity));
        }
    }

    public static void sendEntityUpdate(int user, Object update) {
        if (NetworkComunicator.playerHasConnection(user)) {
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(user), update);
        }
    }

    public static void notifyToNearEntities(int entityId, Object update) {
        MapManager.getNearEntities(entityId).forEach(nearPlayer -> {
            Log.info("Notifying near player " + nearPlayer + ". Update: " + update);
            sendEntityUpdate(nearPlayer, update);
        });
    }

    public static void notifyUpdate(int entityId, Object update) {
        sendEntityUpdate(entityId, update);
        notifyToNearEntities(entityId, update);
    }

    public static World getWorld() {
        return WorldServer.getWorld();
    }
}