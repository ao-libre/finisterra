package server.manager;

import com.artemis.E;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import entity.Heading;
import entity.character.status.Hit;
import server.core.Server;
import server.database.model.attributes.Attributes;
import server.database.model.modifiers.Modifiers;
import shared.interfaces.CharClass;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.network.notifications.RemoveEntity;
import shared.objects.types.*;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

public class WorldManager extends DefaultManager {

    public static final int ATTR_BASE_VALUE = 18;
    private static int MAX_LEVEL = 45;
    private static int STAT_MAXHIT_UNDER36 = 99;
    private static int STAT_MAXHIT_OVER36 = 999;


    public WorldManager(Server server) {
        super(server);
    }

    @Override
    public void init() {

    }

    public int createEntity(String name, int heroId) {
        int player = getWorld().create();

        E entity = E(player);
        // set position
        setEntityPosition(entity);
        // set head and body
        setHeadAndBody(name, entity);
        // set class
        setClassAndAttributes(heroId, entity);
        // set inventory
        setInventory(player);

        return player;
    }

    private void setClassAndAttributes(int heroId, E entity) {
        // set body and head
        Hero hero = Hero.getHeros().get(heroId);
        Race race = Race.values()[hero.getRaceId()];
        setNakedBody(entity, race);
        setHead(entity, race);
        entity.charHeroHeroId(heroId);

        CharClass charClass = CharClass.values()[hero.getClassId()];
        setAttributesAndStats(entity, charClass, race);

    }

    private void setAttributesAndStats(E entity, CharClass charClass, Race race) {
        // set attributes
        entity.agilityValue(ATTR_BASE_VALUE + Attributes.AGILITY.of(race));
        entity.charismaValue(ATTR_BASE_VALUE + Attributes.CHARISMA.of(race));
        entity.constitutionValue(ATTR_BASE_VALUE + Attributes.CONSTITUTION.of(race));
        entity.intelligenceValue(ATTR_BASE_VALUE + Attributes.INTELLIGENCE.of(race));
        entity.strengthValue(ATTR_BASE_VALUE + Attributes.STRENGTH.of(race));

        // set stats
        setLevel(entity);
        setStamina(entity);
        setHP(entity, charClass);
        setMana(entity, charClass);
        setHit(entity, charClass);
    }

    private void setLevel(E entity) {
        entity.levelLevel(MAX_LEVEL);
    }

    private void setStamina(E entity) {
        // set stamina
        entity.staminaMax(100);
        entity.staminaMin(100);
    }

    private void setHit(E entity, CharClass charClass) {
        int minLvl, maxLvl;
        int breakingLvl = 35;
        switch (charClass) {
            case WARRIOR:
            case ARCHER:
                minLvl = 2;
                maxLvl = 3;
                break;
            case PIRATE:
                minLvl = 3;
                maxLvl = 3;
                break;
            case PALADIN:
            case ASSESIN:
            case ROGUE:
                minLvl = 1;
                maxLvl = 3;
                break;
            case CLERIC:
            case BARDIC:
            case DRUID:
            case THIEF:
                minLvl = 2;
                maxLvl = 2;
                break;
            case MAGICIAN:
                minLvl = 1;
                maxLvl = 1;
                break;
            default:
                minLvl = 0;
                maxLvl = 0;
                break;
        }
        int maxHit = Math.min(STAT_MAXHIT_OVER36, Math.min(minLvl * breakingLvl + (entity.getLevel().level - breakingLvl) * maxLvl, STAT_MAXHIT_UNDER36));
        entity.hit();
        Hit hit = entity.getHit();
        hit.setMax(maxHit);
        hit.setMin(maxHit);
    }

    private void setHead(E entity, Race race) {
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

    void setNakedBody(E entity, Race race) {
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

    private void setMana(E entity, CharClass heroClass) {
        int manaBase = 50;
        float manaPerLvlFactor;
        switch (heroClass) {
            case ROGUE:
                manaPerLvlFactor = 1/ 3 * 2;
                break;
            case PALADIN:
            case ASSESIN:
                manaPerLvlFactor = 1;
                break;
            case MAGICIAN:
                manaPerLvlFactor = 2.8f;
                break;
            case BARDIC:
            case CLERIC:
            case DRUID:
                manaPerLvlFactor = 2f;
                break;
            default:
                manaPerLvlFactor = 0;
                break;
        }
        float manaPerLvl = entity.intelligenceValue() * manaPerLvlFactor;
        int maxMana = (int) (manaPerLvl * entity.levelLevel() + manaBase);
        maxMana = maxMana == manaBase ? 0 : maxMana;
        entity.manaMax(maxMana);
        entity.manaMin(maxMana);
    }

    private void setHP(E entity, CharClass charClass) {
        int average = (int) Modifiers.HEALTH.of(charClass) * entity.getLevel().level;
        int maxHP = ThreadLocalRandom.current().nextInt(average - 10, average + 11);
        entity.healthMax(maxHP);
        entity.healthMin(maxHP);
    }

    private void setInventory(int player) {
        E(player).inventory();
        addItem(player, Type.HELMET).ifPresent(helmet -> {
            E(player).helmetIndex(helmet.getId());
        });
        addItem(player, Type.ARMOR).ifPresent(armor -> {
            E(player).armorIndex(armor.getId());
            E(player).bodyIndex(((ArmorObj)armor).getBodyNumber());
        });
        addItem(player, Type.WEAPON).ifPresent(weapon -> {
            E(player).weaponIndex(weapon.getId());
        });
        addItem(player, Type.SHIELD).ifPresent(shield -> {
            E(player).shieldIndex(shield.getId());
        });
        addPotion(player, PotionKind.HP);
        addPotion(player, PotionKind.MANA);
    }

    private void setHeadAndBody(String name, E entity) {
        entity
                .headingCurrent(Heading.HEADING_NORTH)
                .character()
                .nameText(name);
    }

    private void setEntityPosition(E entity) {
        entity
                .worldPosX(25)
                .worldPosY(25)
                .worldPosMap(1);
    }

    private void addPotion(int player, PotionKind kind) {
        Set<Obj> objs = getServer().getObjectManager().getTypeObjects(Type.POTION);
        objs.stream() //
                .map(PotionObj.class::cast) //
                .filter(potion -> {
                    PotionKind potionKind = potion.getKind();
                    return potionKind != null && potionKind.equals(kind);
                }) //
                .findFirst() //
                .ifPresent(obj -> E(player).getInventory().add(obj.getId(), false));
    }

    private Optional<Obj> addItem(int player, Type type) {
        Set<Obj> objs = getServer().getObjectManager().getTypeObjects(type);
        Optional<Obj> result = objs.stream()
                .filter(obj -> {
                    if (obj instanceof ObjWithClasses) {
                        int heroId = E(player).getCharHero().heroId;
                        Hero hero = Hero.getHeros().get(heroId);
                        CharClass clazz = CharClass.values()[hero.getClassId()];
                        Set<CharClass> forbiddenClasses = ((ObjWithClasses) obj).getForbiddenClasses();
                        boolean supported = forbiddenClasses.size() == 0 || !forbiddenClasses.contains(clazz);
                        if (supported && obj instanceof ArmorObj) {
                            if (((ArmorObj) obj).isDwarf()) {
                                Race race = Race.values()[hero.getRaceId()];
                                supported = race.equals(Race.GNOME) || race.equals(Race.DWARF);
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
                .findFirst();
        result.ifPresent(obj -> E(player).getInventory().add(obj.getId(), true));
        return result;
    }

    public void registerItem(int id) {
        getServer().getMapManager().updateEntity(id);
    }

    public void registerEntity(int connectionId, int id) {
        getServer().getNetworkManager().registerUserConnection(id, connectionId);
        getServer().getMapManager().updateEntity(id);
    }

    public void unregisterEntity(int playerToDisconnect) {
        getWorld().delete(playerToDisconnect);
    }

    void sendEntityRemove(int user, int entity) {
        if (getServer().getNetworkManager().playerHasConnection(user)) {
            getServer().getNetworkManager().sendTo(getServer().getNetworkManager().getConnectionByPlayer(user), new RemoveEntity(entity));
        }
    }

    public void sendEntityUpdate(int user, Object update) {
        if (getServer().getNetworkManager().playerHasConnection(user)) {
            getServer().getNetworkManager().sendTo(getServer().getNetworkManager().getConnectionByPlayer(user), update);
        }
    }

    public void notifyToNearEntities(int entityId, Object update) {
        getServer().getMapManager().getNearEntities(entityId).forEach(nearPlayer -> {
            sendEntityUpdate(nearPlayer, update);
        });
    }

    public void notifyUpdate(int entityId, Object update) {
        sendEntityUpdate(entityId, update);
        notifyToNearEntities(entityId, update);
    }

    private World getWorld() {
        return getServer().getWorld();
    }
}