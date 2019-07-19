package server.systems;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import entity.character.states.Heading;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import position.WorldPos;
import server.database.model.attributes.Attributes;
import server.systems.manager.NPCManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.SpellManager;
import server.systems.manager.WorldManager;
import shared.interfaces.CharClass;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.model.Spell;
import shared.model.lobby.Team;
import shared.model.npcs.NPC;
import shared.objects.types.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static server.systems.CharacterTrainingSystem.DEFAULT_STAMINA;
import static server.systems.CharacterTrainingSystem.INITIAL_LEVEL;

@Wire
public class EntityFactorySystem extends PassiveSystem {

    private static final int INITIAL_EXP_TO_NEXT_LEVEL = 300;
    private static final int ATTR_BASE_VALUE = 18;

    private WorldManager worldManager;
    private ObjectManager objectManager;
    private SpellManager spellManager;


    public void createObject(int objIndex, int objCount, WorldPos pos) {
        int objId = world.create();
        E(objId).worldPosMap(pos.map)
                .worldPosX(pos.x)
                .worldPosY(pos.y)
                .objectIndex(objIndex)
                .objectCount(objCount);
        worldManager.registerEntity(objId);
    }

    public void createNPC(int npcIndex, WorldPos pos) {
        NPC npc = world.getSystem(NPCManager.class).getNpcs().get(npcIndex);
        int npcId = world.create();

        E npcEntity = E(npcId);
        npcEntity
                .nPCId(npcIndex)
                .bodyIndex(npc.getBody())
                .headingCurrent(Heading.HEADING_SOUTH)
                .nameText(npc.getName());
        if (npc.getMovement() == 3) {
            npcEntity.aOPhysics().aOPhysicsVelocity(85f);
            npcEntity.aIMovement();
        }
        if (npc.getHead() > 0) {
            npcEntity.headIndex(npc.getHead());
        }
        if (npc.isCommerce()) {
            npcEntity.commerce();
        }
        if (npc.isHostile()) {
            npcEntity.hostile();
        }
        if (npc.getMaxHit() > 0) {
            npcEntity.hit().hitMax(npc.getMaxHit()).hitMin(npc.getMinHit());
        }
        if (npc.getEvasionPower() > 0) {
            npcEntity.evasionPowerValue(npc.getEvasionPower());
        }
        if (npc.getAttackPower() > 0) {
            npcEntity.attackPowerValue(npc.getAttackPower());
        }
        if (npc.getMaxHP() > 0) {
            npcEntity.health().healthMin(npc.getMinHP()).healthMax(npc.getMaxHP());
        }
        if (npc.isAttackable()) {
            npcEntity.attackable();
        }

        npcEntity.originPosMap(pos.map).originPosX(pos.x).originPosY(pos.y);
        npcEntity.worldPosMap(pos.map).worldPosX(pos.x).worldPosY(pos.y);
        worldManager.registerEntity(npcId);
    }

    public int createPlayer(String name, Hero hero, Team team) {
        int player = getWorld().create();

        E entity = E(player);
        entity.character();
        switch (team) {
            case NO_TEAM:
                entity.gM();
                break;
            case CAOS_ARMY:
                entity.criminal();
                break;
        }
        entity.charHeroHeroId(hero.ordinal());
        // set position
        setEntityPosition(entity);
        // set head and body
        setHeadAndBody(name, entity);
        // set class
        setClassAndAttributes(hero, entity);
        // set inventory
        setInventory(player, hero, team);
        // set spells
        setSpells(player, hero);

        return player;
    }


    private void setClassAndAttributes(Hero hero, E entity) {
        // set body and head
        Race race = Race.of(entity);
        setNakedBody(entity, race);
        setHead(entity, race);
        entity.charHeroHeroId(hero.ordinal());
        setAttributesAndStats(entity, race);
    }

    private void setAttributesAndStats(E entity, Race race) {
        // set attributes
        entity.agilityBaseValue(ATTR_BASE_VALUE + Attributes.AGILITY.of(race));
        entity.agilityCurrentValue(ATTR_BASE_VALUE + Attributes.AGILITY.of(race));
        entity.charismaBaseValue(ATTR_BASE_VALUE + Attributes.CHARISMA.of(race));
        entity.constitutionBaseValue(ATTR_BASE_VALUE + Attributes.CONSTITUTION.of(race));
        entity.intelligenceBaseValue(ATTR_BASE_VALUE + Attributes.INTELLIGENCE.of(race));
        entity.strengthBaseValue(ATTR_BASE_VALUE + Attributes.STRENGTH.of(race));
        entity.strengthCurrentValue(ATTR_BASE_VALUE + Attributes.STRENGTH.of(race));

        // set stats
        setLevel(entity);
        setStamina(entity);
        setHP(entity);
        setMana(entity);
        setHit(entity);
        entity.goldCount(0);
    }

    private void setLevel(E entity) {
        entity.levelLevel(INITIAL_LEVEL).levelExp(0).levelExpToNextLevel(INITIAL_EXP_TO_NEXT_LEVEL);
    }

    private void setStamina(E entity) {
        // set stamina
        int stamina = 20 * entity.agilityCurrentValue() / 6;
        entity.staminaMax(stamina).staminaMin(stamina);
    }

    private void setHit(E entity) {
        entity.hitMax(2).hitMin(1);
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

    public void setNakedBody(E entity, Race race) {
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

    private void setMana(E entity) {
        CharClass charClass = CharClass.of(entity);
        int mana = 300; //Bonus 300 mana para tirar inmovilizar
        switch (charClass) {
            case MAGICIAN:
                mana = entity.intelligenceBaseValue() * 3 + mana;
                break;
            case CLERIC:
            case DRUID:
            case BARDIC:
                mana = entity.intelligenceBaseValue() * 2 + mana;
                break;
            case ASSASSIN:
            case PALADIN:
                mana = entity.intelligenceBaseValue() + mana;
                break;
        }
        entity.manaMax(mana);
        entity.manaMin(mana);
    }

    private void setHP(E entity) {
        int random = ThreadLocalRandom.current().nextInt(1, entity.constitutionBaseValue() / 3);
        entity.healthMax(DEFAULT_STAMINA + random);
        entity.healthMin(DEFAULT_STAMINA + random);
    }

    private void setInventory(int player, Hero hero, Team team) {
        E(player).inventory();
        addPotion(player, PotionKind.HP);

        if (E(player).manaMax() > 0) {
            addPotion(player, PotionKind.MANA);
        } else {
            addPotion(player, PotionKind.STRENGTH);
        }

        getHelmet(hero).ifPresent(helmet -> {
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


    private void addPotion(int player, PotionKind kind) {
        Set<Obj> objs = objectManager.getTypeObjects(Type.POTION);
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

    private Optional<Obj> getHelmet(Hero hero) {
        Optional<Obj> result = Optional.empty();
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
        switch (hero) {
            case PALADIN:
            case GUERRERO:
                objectManager.getObject(403).ifPresent(result::add);
                break;
            case ARQUERO:
                objectManager.getObject(665).ifPresent(result::add);
                objectManager.getObject(366).ifPresent(result::add);
                break;
            case BARDO:
                objectManager.getObject(366).ifPresent(result::add);
                break;
            case CLERIGO:
                objectManager.getObject(129).ifPresent(result::add);
                break;
            case ASESINO:
                objectManager.getObject(559).ifPresent(result::add);
                break;
            case MAGO:
                objectManager.getObject(660).ifPresent(result::add);
                break;
            default:
                break;
        }
        return result;
    }


    @Deprecated
    private Optional<Obj> addItem(int player, Type type) {
        Set<Obj> objs = objectManager.getTypeObjects(type);
        Optional<Obj> result = objs.stream()
                .filter(obj -> {
                    if (obj instanceof ObjWithClasses) {
                        int heroId = E(player).getCharHero().heroId;
                        CharClass clazz = CharClass.of(E(player));
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
            CharClass clazz = CharClass.of(E(player));
            Set<CharClass> forbiddenClasses = ((ObjWithClasses) obj).getForbiddenClasses();
            Log.info("Item found for class: " + clazz.name() + " and forbidden classes are: " + Arrays
                    .toString(forbiddenClasses.toArray()));
            E(player).getInventory().add(obj.getId(), true);
        });
        return result;
    }

    private void setSpells(int player, Hero hero) {
        Set<Spell> spells = getSpells(hero);
        final Integer[] spellIds = spells
                .stream()
                .map(spell -> spellManager.getId(spell))
                .toArray(Integer[]::new);
        E(player).spellBookSpells(spellIds);
    }

    private Set<Spell> getSpells(Hero hero) {
        final Map<Integer, Spell> spells = spellManager.getSpells();
        Set<Spell> result = new HashSet<>();
        Spell apoca = spells.get(25);
        Spell desca = spells.get(23);
        Spell tormenta = spells.get(15);
        Spell misil = spells.get(8);
        Spell inmo = spells.get(24);
        Spell remo = spells.get(10);
        Spell curar = spells.get(3);
        switch (hero) {
            case BARDO:
            case CLERIGO:
                result.add(curar);
            case MAGO:
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


    private void setEntityPosition(E entity) {
        switch (Race.of(entity)) {
            case DROW:
                entity.worldPosX(62).worldPosY(68).worldPosMap(1);
                break;
            case ELF:
                entity.worldPosX(43).worldPosY(16).worldPosMap(2);
                break;
            case DWARF:
                entity.worldPosX(46).worldPosY(9).worldPosMap(40);
                break;
            case GNOME:
                entity.worldPosX(33).worldPosY(49).worldPosMap(1);
                break;
            case HUMAN:
                entity.worldPosX(65).worldPosY(17).worldPosMap(1);
                break;
        }
    }

    private WorldPos getValidPosition(int map) {
        //TODO comprobar que la posición este libre
        return new WorldPos(50, 50, map);
    }
}