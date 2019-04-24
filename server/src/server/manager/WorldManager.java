package server.manager;

import camera.Focused;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import entity.character.states.CanWrite;
import entity.character.states.Heading;
import entity.character.status.Hit;
import map.Cave;
import physics.AOPhysics;
import position.WorldPos;
import server.core.Server;
import server.database.model.attributes.Attributes;
import server.database.model.modifiers.Modifiers;
import shared.interfaces.CharClass;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.model.Spell;
import shared.model.lobby.Player;
import shared.model.lobby.Team;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;
import shared.objects.types.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

public class WorldManager extends DefaultManager {

    private static final int ATTR_BASE_VALUE = 18;
    private static int MAX_LEVEL = 45;
    private static int STAT_MAXHIT_UNDER36 = 99;
    private static int STAT_MAXHIT_OVER36 = 999;

    public WorldManager(Server server) {
        super(server);
    }

    @Override
    public void init() {

    }

    public int createEntity(String name, int heroId, Team team) {
        int player = getWorld().create();

        E entity = E(player);
        switch (team) {
            case NO_TEAM:
                entity.gM();
                break;
            case CAOS_ARMY:
                entity.criminal();
                break;
        }
        // set position
        setEntityPosition(entity);
        // set head and body
        setHeadAndBody(name, entity);
        // set class
        setClassAndAttributes(heroId, entity);
        // set inventory
        setInventory(player, Hero.getHeroes().get(heroId), team);
        // set spells
        setSpells(player, Hero.getHeroes().get(heroId));

        return player;
    }

    private void setSpells(int player, Hero hero) {
        Set<Spell> spells = getSpells(hero);
        final List<Integer> ids = spells
            .stream()
            .map(spell -> getServer().getSpellManager().getId(spell))
            .collect(Collectors.toList());
        final Integer[] spellIds = ids.toArray(new Integer[0]);
        E(player).spellBookSpells(spellIds);
    }

    private Set<Spell> getSpells(Hero hero) {
        final Map<Integer, Spell> spells = getServer().getSpellManager().getSpells();
        Set<Spell> result = new HashSet<>();
        Spell apoca = spells.get(25);
        Spell desca = spells.get(23);
        Spell tormenta = spells.get(15);
        Spell misil = spells.get(8);
        Spell inmo = spells.get(24);
        Spell remo = spells.get(10);
        switch (hero) {
            case MAGO:
            case BARDO:
            case CLERIGO:
                result.add(apoca);
                break;
        }
        if (!(hero.equals(Hero.GUERRERO) || hero.equals(Hero.ARQUERO))) {
            result.add(misil);
            result.add(tormenta);
            result.add(desca);
            result.add(inmo);
            result.add(remo);
        }
        return result;
    }

    private void setClassAndAttributes(int heroId, E entity) {
        // set body and head
        final List<Hero> heroes = Hero.getHeroes();
        Hero hero = heroes.size() > heroId ? heroes.get(heroId) : heroes.get(0);
        Race race = Race.values()[hero.getRaceId()];
        setNakedBody(entity, race);
        setHead(entity, race);
        entity.charHeroHeroId(heroId);

        CharClass charClass = CharClass.values()[hero.getClassId()];
        setAttributesAndStats(entity, charClass, race);
    }

    private void setAttributesAndStats(E entity, CharClass charClass, Race race) {
        // set attributes
        //        entity.agilityValue(ATTR_BASE_VALUE + Attributes.AGILITY.of(race));
        entity.agilityValue(38);
        entity.charismaValue(ATTR_BASE_VALUE + Attributes.CHARISMA.of(race));
        entity.constitutionValue(ATTR_BASE_VALUE + Attributes.CONSTITUTION.of(race));
        entity.intelligenceValue(ATTR_BASE_VALUE + Attributes.INTELLIGENCE.of(race));
        //        entity.strengthValue(ATTR_BASE_VALUE + Attributes.STRENGTH.of(race));
        entity.strengthValue(38);

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
            case ASSASSIN:
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
        int maxHit = Math.min(STAT_MAXHIT_OVER36,
                              Math.min(minLvl * breakingLvl + (entity.getLevel().level - breakingLvl) * maxLvl,
                                       STAT_MAXHIT_UNDER36));
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
                manaPerLvlFactor = 1 / 3 * 2;
                break;
            case PALADIN:
            case ASSASSIN:
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
        int maxMana = (int) (manaPerLvl * (entity.levelLevel() - 1) + manaBase);
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

    private void setInventory(int player, Hero hero, Team team) {
        E(player).inventory();
        addPotion(player, PotionKind.HP);
        if (E(player).manaMax() > 0) {
            addPotion(player, PotionKind.MANA);
        }
        getHelmet(hero, team).ifPresent(helmet -> {
            E(player).helmetIndex(helmet.getId());
            E(player).getInventory().add(helmet.getId(), true);
        });
        getArmor(hero, team).ifPresent(armor -> {
            E(player).armorIndex(armor.getId());
            E(player).bodyIndex(((ArmorObj) armor).getBodyNumber());
            E(player).getInventory().add(armor.getId(), true);
        });
        final Set<Obj> weapons = getWeapon(hero, team);
        if (!weapons.isEmpty()) {
            final Obj next = weapons.iterator().next();
            E(player).getInventory().add(next.getId(), true);
            E(player).weaponIndex(next.getId());
            weapons.forEach(weapon -> {
                if (weapon != next) {
                    E(player).getInventory().add(weapon.getId(), false);
                }
            });
        }

        getShield(hero, team).ifPresent(shield -> {
            E(player).shieldIndex(shield.getId());
            E(player).getInventory().add(shield.getId(), true);
        });
    }

    private void setHeadAndBody(String name, E entity) {
        entity
            .headingCurrent(Heading.HEADING_NORTH)
            .character()
            .nameText(name);
    }

    private void setEntityPosition(E entity) {
        WorldPos worldPos = getValidPosition(1);
        entity
            .worldPosX(worldPos.x)
            .worldPosY(worldPos.y)
            .worldPosMap(worldPos.map);
    }

    private WorldPos getValidPosition(int map) {
        final E entity = E(getServer().getMapManager().mapEntity);
        if (entity.hasCave()) {
            final Cave cave = entity.getCave();
            final int midHeight = cave.height / 2;
            final int midWidth = cave.width / 2;
            WorldPos validPos = getRandomPos(midWidth, midHeight, map);
            while (cave.isBlocked(validPos.x, validPos.y)) {
                validPos = getRandomPos(cave.width, cave.height, map);
            }
            return validPos;
        }
        return new WorldPos(1, 1, 1);
    }

    private WorldPos getRandomPos(int maxWidth, int maxHeight, int map) {
        final int x = ThreadLocalRandom.current().nextInt(maxWidth);
        final int y = ThreadLocalRandom.current().nextInt(maxHeight);
        return new WorldPos(x, y, map);
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

    private Optional<Obj> getArmor(Hero hero, Team team) {
        final Random random = new Random();
        Optional<Obj> result = Optional.empty();
        final ObjectManager objectManager = getServer().getObjectManager();
        List<Integer> noTeam;
        List<Integer> real;
        List<Integer> chaos;
        List<Integer> set;
        switch (hero) {
            case PALADIN:
                noTeam = Arrays.asList(195, 485, 496);
                real = Collections.singletonList(680);
                chaos = Collections.singletonList(683);

                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectManager.getObject(set.get(random.nextInt(set.size())));
                break;
            case GUERRERO:
                noTeam = Arrays.asList(243, 968);
                real = Arrays.asList(681, 694);
                chaos = Arrays.asList(685, 695);

                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectManager.getObject(set.get(random.nextInt(set.size())));
                break;
            case MAGO:
                noTeam = Arrays.asList(525, 969);
                real = Arrays.asList(549, 682);
                chaos = Arrays.asList(558, 686);

                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectManager.getObject(set.get(random.nextInt(set.size())));
                break;
            case BARDO:
                noTeam = Arrays.asList(519, 359, 484);
                real = Collections.singletonList(520);
                chaos = Collections.singletonList(523);

                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectManager.getObject(set.get(random.nextInt(set.size())));
                break;
            case ARQUERO:
                noTeam = Arrays.asList(964, 965);
                real = Collections.singletonList(1040);
                chaos = Collections.singletonList(1041);

                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectManager.getObject(set.get(random.nextInt(set.size())));
                break;
            case ASESINO:
                noTeam = Arrays.asList(356, 495);
                real = Arrays.asList(521, 691);
                chaos = Arrays.asList(684, 701);

                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectManager.getObject(set.get(random.nextInt(set.size())));
                break;
            case CLERIGO:
                noTeam = Arrays.asList(356, 495);
                real = Collections.singletonList(521);
                chaos = Collections.singletonList(523);

                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectManager.getObject(set.get(random.nextInt(set.size())));
                break;
        }
        return result;
    }

    private Optional<Obj> getHelmet(Hero hero, Team team) {
        Optional<Obj> result = Optional.empty();
        final ObjectManager objectManager = getServer().getObjectManager();
        switch (hero) {
            case PALADIN:
            case GUERRERO:
                result = objectManager.getObject(405);
                break;
            case ARQUERO:
                Random random = new Random();
                result = objectManager.getObject(Arrays.asList(1052, 1003).get(random.nextInt(2)));
                break;
            case CLERIGO:
            case ASESINO:
                result = objectManager.getObject(131);
                break;
            case BARDO:
            case MAGO:
                result = objectManager.getObject(851);
                break;
            default:
                break;
        }
        return result;
    }

    private Optional<Obj> getShield(Hero hero, Team team) {
        Optional<Obj> result = Optional.empty();
        final ObjectManager objectManager = getServer().getObjectManager();
        switch (hero) {
            case PALADIN:
            case CLERIGO:
            case GUERRERO:
                result = objectManager.getObject(team.equals(Team.NO_TEAM) ? 130 : team.equals(Team.REAL_ARMY) ? 1038 : 1037);
                break;
            case BARDO:
            case ASESINO:
                result = objectManager.getObject(404);
                break;
            default:
                break;
        }
        return result;
    }

    private Set<Obj> getWeapon(Hero hero, Team type) {
        Set<Obj> result = new HashSet<>();
        final ObjectManager objectManager = getServer().getObjectManager();
        switch (hero) {
            case PALADIN:
            case GUERRERO:
                result.add(objectManager.getObject(403).get());
                break;
            case ARQUERO:
                result.add(objectManager.getObject(665).get());
                result.add(objectManager.getObject(366).get());
                break;
            case BARDO:
                result.add(objectManager.getObject(365).get());
                break;
            case CLERIGO:
                result.add(objectManager.getObject(129).get());
                break;
            case ASESINO:
                result.add(objectManager.getObject(367).get());
                break;
            case MAGO:
                result.add(objectManager.getObject(660).get());
                break;
            default:
                break;
        }
        return result;
    }



    private Optional<Obj> addItem(int player, Type type) {
        Set<Obj> objs = getServer().getObjectManager().getTypeObjects(type);
        Optional<Obj> result = objs.stream()
            .filter(obj -> {
                if (obj instanceof ObjWithClasses) {
                    int heroId = E(player).getCharHero().heroId;
                    CharClass clazz = CharClass.get(E(player));
                    Set<CharClass> forbiddenClasses = ((ObjWithClasses) obj).getForbiddenClasses();
                    boolean supported = forbiddenClasses.size() == 0 || !forbiddenClasses.contains(clazz);
                    if (supported && obj instanceof ArmorObj) {
                        Race race = Race.of(E(player));
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
                return false;
            })
            .max(this::getComparator);
        result.ifPresent(obj -> {
            CharClass clazz = CharClass.get(E(player));
            Set<CharClass> forbiddenClasses = ((ObjWithClasses) obj).getForbiddenClasses();
            Log.info("Item found for class: " + clazz.name() + " and forbidden classes are: " + Arrays
                .toString(forbiddenClasses.toArray()));
            E(player).getInventory().add(obj.getId(), true);
        });
        return result;
    }

    private int getComparator(Obj obj1, Obj obj2) {
        if (obj1 instanceof ArmorObj) {
            ArmorObj armor1 = (ArmorObj) obj1;
            ArmorObj armor2 = (ArmorObj) obj2;
            return armor1.getMaxDef() - armor2.getMaxDef();
        } else if (obj1 instanceof WeaponObj) {
            WeaponObj weapon1 = (WeaponObj) obj1;
            WeaponObj weapon2 = (WeaponObj) obj2;
            return weapon1.getMaxHit() - weapon2.getMaxHit();
        } else if (obj1 instanceof ShieldObj) {
            ShieldObj shield1 = (ShieldObj) obj1;
            ShieldObj shield2 = (ShieldObj) obj2;
            return shield1.getMaxDef() - shield2.getMaxDef();
        } else if (obj1 instanceof HelmetObj) {
            HelmetObj helmet1 = (HelmetObj) obj1;
            HelmetObj helmet2 = (HelmetObj) obj2;
            return helmet1.getMaxDef() - helmet2.getMaxDef();
        }
        return obj1.getName().compareTo(obj2.getName());
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
            getServer().getNetworkManager()
                .sendTo(getServer().getNetworkManager().getConnectionByPlayer(user), new RemoveEntity(entity));
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

    public void userDie(int entityId) {
        final E e = E(entityId);
        final String name = e.getName().text;
        final int connectionByPlayer = getServer().getNetworkManager().getConnectionByPlayer(entityId);
        getServer().getMapManager().removeEntity(entityId);
        getServer().getWorldManager().sendEntityRemove(entityId, entityId);
        login(connectionByPlayer, new Player(connectionByPlayer, name, Hero.getRandom()));
        getServer().getWorldManager().unregisterEntity(entityId);
    }

    public void login(int connectionId, Player player) {
            final int entity = createEntity(player.getPlayerName(), player.getHero().ordinal(), player.getTeam());
            List<Component> components = WorldUtils(getWorld()).getComponents(getWorld().getEntity(entity));
            components.add(new Focused());
            components.add(new AOPhysics());
            components.add(new CanWrite());
            getServer().getNetworkManager().sendTo(connectionId, new EntityUpdate(entity, components.toArray(new Component[0]), new Class[0]));
            registerEntity(connectionId, entity);
    }
}
