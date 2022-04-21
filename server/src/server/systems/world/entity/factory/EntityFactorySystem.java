package server.systems.world.entity.factory;

import com.artemis.*;
import component.entity.character.Character;
import component.entity.character.attributes.*;
import component.entity.character.equipment.Armor;
import component.entity.character.equipment.Helmet;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.info.*;
import component.entity.character.parts.Body;
import component.entity.character.parts.Head;
import component.entity.character.states.Heading;
import component.entity.character.status.*;
import component.entity.world.Object;
import component.position.WorldPos;
import component.position.WorldPosOffsets;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.database.model.attributes.Attributes;
import server.systems.config.NPCSystem;
import server.systems.config.ObjectSystem;
import server.systems.config.SpellSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.ai.PathFindingSystem;
import shared.interfaces.CharClass;
import shared.interfaces.Hero;
import shared.interfaces.Race;
import shared.model.Spell;
import shared.model.npcs.NPC;
import shared.model.npcs.NPCToEntity;
import shared.objects.types.*;
import shared.util.MapHelper;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static server.systems.world.entity.training.CharacterTrainingSystem.DEFAULT_STAMINA;
import static server.systems.world.entity.training.CharacterTrainingSystem.INITIAL_LEVEL;

// @todo: El sistema es muy grande y quizá podría modularizarse
// @todo: Evaluar si conviene usar EntityTransmuter para crear nuevas entidades con tantos componentes.
// @todo: La clase se llama EntityFactory pero parece crear solo PJs.
public class EntityFactorySystem extends PassiveSystem {

    private static final int INITIAL_EXP_TO_NEXT_LEVEL = 300;
    private static final int ATTR_BASE_VALUE = 18;

    private MapSystem mapSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private ObjectSystem objectSystem;
    private SpellSystem spellSystem;
    private PathFindingSystem pathFindingSystem;
    private NPCSystem npcSystem;

    // Atributos
    ComponentMapper<Agility> mAgility;
    ComponentMapper<Charisma> mCharisma;
    ComponentMapper<Constitution> mConstitution;
    ComponentMapper<Intelligence> mIntelligence;
    ComponentMapper<Strength> mStrength;

    // Equipment
    ComponentMapper<Armor> mArmor;
    ComponentMapper<Helmet> mHelmet;
    ComponentMapper<Shield> mShield;
    ComponentMapper<Weapon> mWeapon;

    ComponentMapper<Bag> mBag;
    ComponentMapper<Body> mBody;
    ComponentMapper<Character> mCharacter;
    ComponentMapper<CharHero> mCharHero;
    ComponentMapper<Gold> mGold;
    ComponentMapper<Head> mHead;
    ComponentMapper<Heading> mHeading;
    ComponentMapper<Health> mHealth;
    ComponentMapper<Hit> mHit;
    ComponentMapper<Level> mLevel;
    ComponentMapper<Mana> mMana;
    ComponentMapper<Name> mName;
    ComponentMapper<Skills> mSkills;
    ComponentMapper<SpellBook> mSpellBook;
    ComponentMapper<Stamina> mStamina;
    ComponentMapper<Object> mObject;
    ComponentMapper<WorldPos> mWorldPos;
    ComponentMapper<WorldPosOffsets> mWorldPosOffsets;

    // Create entity with current components
    public int create(Collection<? extends Component> components) {
        Entity entity = world.createEntity();
        EntityEdit edit = entity.edit();
        components.forEach(edit::add);
        return entity.getId();
    }

    public void createObject(int objIndex, int objCount, WorldPos pos) {
        int objectId = world.create();
        Object object = mObject.create(objectId);
        object.setIndex(objIndex);
        object.setCount(objCount);
        setWorldPosition(objectId, pos, true);
        worldEntitiesSystem.registerEntity(objectId);
    }

    public void createNPC(int npcIndex, WorldPos pos) {
        NPC npc = npcSystem.getNpcs().get(npcIndex);
        int npcId = NPCToEntity.getNpcEntity(world, npcIndex, pos, npc);
        worldEntitiesSystem.registerEntity(npcId);
    }

    public int create(String username, int heroId) {
        Hero hero = Hero.values()[heroId];
        Race race = Race.values()[hero.getRaceId()];

        int playerId = getWorld().create();

        Character character = mCharacter.create(playerId);
        Name name = mName.create(playerId);
        name.setText(username);

        Heading heading = mHeading.create(playerId);
        heading.setCurrent(Heading.HEADING_SOUTH);

        CharHero charHero = mCharHero.create(playerId);
        charHero.setHeroId(heroId);

        setEntityPosition(playerId);
        setAttributesAndStats(playerId, race);
        setHead(playerId, race);
        setNakedBody(playerId, race);
        // set inventory
        setInventory(playerId, hero);
        // set spells
        setSpells(playerId, hero);
        setSkills(playerId, 100);

        return playerId;
    }

    private void setSkills(int entityId, int initial) {
        mSkills.create(entityId).initial(initial);
    }

    private void setAttributesAndStats(int entityId, Race race) {
        // Valores base de los atributos
        int agilityValue = ATTR_BASE_VALUE + Attributes.AGILITY.of(race);
        int charismaValue = ATTR_BASE_VALUE + Attributes.CHARISMA.of(race);
        int constitutionValue = ATTR_BASE_VALUE + Attributes.CONSTITUTION.of(race);
        int intelligenceValue = ATTR_BASE_VALUE + Attributes.INTELLIGENCE.of(race);
        int strengthValue = ATTR_BASE_VALUE + Attributes.STRENGTH.of(race);

        // set attributes
        Agility agility = mAgility.create(entityId);
        agility.setBaseValue(agilityValue);
        agility.setCurrentValue(agilityValue);

        Charisma charisma = mCharisma.create(entityId);
        charisma.setBaseValue(charismaValue);
        charisma.setCurrentValue(charismaValue);

        Constitution constitution = mConstitution.create(entityId);
        constitution.setBaseValue(constitutionValue);
        constitution.setCurrentValue(constitutionValue);

        Intelligence intelligence = mIntelligence.create(entityId);
        intelligence.setBaseValue(intelligenceValue);
        intelligence.setCurrentValue(intelligenceValue);

        Strength strength = mStrength.create(entityId);
        strength.setBaseValue(strengthValue);
        strength.setCurrentValue(strengthValue);

        // set stats
        setLevel(entityId);
        setStamina(entityId);
        setHP(entityId);
        setMana(entityId);
        setHit(entityId);

        Gold gold = mGold.create(entityId);
        gold.setCount(1000);
    }

    private void setLevel(int entityId) {
        Level level = mLevel.create(entityId);
        level.setLevel(INITIAL_LEVEL);
        level.setExp(0);
        level.setExpToNextLevel(INITIAL_EXP_TO_NEXT_LEVEL);
    }

    private void setStamina(int entityId) {
        // set stamina
        int maxStamina = 20 * mAgility.get(entityId).getCurrentValue() / 6;
        Stamina stamina = mStamina.create(entityId);
        stamina.setMax(maxStamina);
        stamina.setMin(maxStamina);
    }

    private void setHit(int entityId) {
        Hit hit = mHit.create(entityId);
        hit.setMin(1);
        hit.setMax(2);
    }

    //TODO cambiado a public para poder resetear las heads antes era private, volver a modificar cuando se guarden en DB las cabezas de los pj
    public void setHead(int entityId, Race race) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        //TODO onlyWoman desde init.json
        int headIndex = 0;
        switch (race) {
            case HUMAN:
                //TODO if onlyWoman = 1 set body woman
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
        mHead.create(entityId).setIndex(headIndex);
    }

    public void setNakedBody(int entityId, Race race) {
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
        mBody.create(entityId).setIndex(bodyIndex);
    }

    private void setMana(int entityId) {
        // @todo Revisar esto y los enums CharClass, Hero y Race
        // CharClass charClass = CharClass.of(entityId);
        int heroId = mCharHero.get(entityId).getHeroId();
        Hero hero = Hero.values()[heroId];
        CharClass charClass = CharClass.values()[hero.getClassId()];

        int manaValue = 300; //Bonus 300 mana para tirar inmovilizar
        switch (charClass) {
            case MAGICIAN:
                manaValue += mIntelligence.get(entityId).getBaseValue() * 3;
                break;
            case CLERIC:
            case DRUID:
            case BARDIC:
                manaValue += mIntelligence.get(entityId).getBaseValue() * 2;
                break;
            case ASSASSIN:
            case PALADIN:
                manaValue += mIntelligence.get(entityId).getBaseValue();
                break;
        }
        Mana mana = mMana.create(entityId);
        mana.setMin(manaValue);
        mana.setMax(manaValue);
    }

    private void setHP(int entityId) {
        int random = ThreadLocalRandom.current().nextInt(1, mConstitution.get(entityId).getBaseValue() / 3);
        Health health = mHealth.create(entityId);
        health.setMin(DEFAULT_STAMINA + random);
        health.setMax(DEFAULT_STAMINA + random);
    }

    private void setInventory(int entityId, Hero hero) {
        Bag bag = mBag.create(entityId);
        addPotion(entityId, PotionKind.HP);
        bag.add(480, true); // flechas

        if (mMana.get(entityId).max > 0) {
            addPotion(entityId, PotionKind.MANA);
        } else {
            addPotion(entityId, PotionKind.STRENGTH);
        }

        getHelmet(hero).ifPresent(helmet -> {
            mHelmet.create(entityId).setIndex(helmet.getId());
            bag.add(helmet.getId(), true);
        });
        getArmor(hero).ifPresent(armor -> {
            mArmor.create(entityId).setIndex(armor.getId());
            mBody.get(entityId).setIndex(((ArmorObj) armor).getBodyNumber());
            bag.add(armor.getId(), true);
        });
        final Set<Obj> weapons = getWeapon(hero);
        if (!weapons.isEmpty()) {
            final Obj next = weapons.iterator().next();
            bag.add(next.getId(), true);
            mWeapon.create(entityId).setIndex(next.getId());
            weapons.forEach(weapon -> {
                if (weapon != next) {
                    bag.add(weapon.getId(), false);
                }
            });
        }

        getShield(hero).ifPresent(shield -> {
            mShield.create(entityId).setIndex(shield.getId());
            bag.add(shield.getId(), true);
        });
    }

    private void addPotion(int entityId, PotionKind kind) {
        Set<Obj> objs = objectSystem.getTypeObjects(Type.POTION);
        objs.stream() //
                .map(PotionObj.class::cast) //
                .filter(potion -> {
                    PotionKind potionKind = potion.getKind();
                    return potionKind != null && potionKind.equals(kind);
                }) //
                .findFirst() //
                .ifPresent(obj -> mBag.get(entityId).add(obj.getId(), false));
    }

    private Optional<Obj> getArmor(Hero hero) {
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

//                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                set = noTeam;
                result = objectSystem.getObject(set.get(random.nextInt(set.size())));
                break;
            case GUERRERO:
                noTeam = Arrays.asList(243, 968);
                real = Arrays.asList(681, 694);
                chaos = Arrays.asList(685, 695);

                set = noTeam;
//                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectSystem.getObject(set.get(random.nextInt(set.size())));
                break;
            case MAGO:
                noTeam = Arrays.asList(525, 969);
                real = Arrays.asList(549, 682);
                chaos = Arrays.asList(558, 686);
                set = noTeam;
//                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectSystem.getObject(set.get(random.nextInt(set.size())));
                break;
            case BARDO:
                noTeam = Arrays.asList(519, 359, 484);
                real = Collections.singletonList(520);
                chaos = Collections.singletonList(523);
                set = noTeam;
//                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectSystem.getObject(set.get(random.nextInt(set.size())));
                break;
            case ARQUERO:
                noTeam = Arrays.asList(964, 965);
                real = Collections.singletonList(1040);
                chaos = Collections.singletonList(1041);
                set = noTeam;
//                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectSystem.getObject(set.get(random.nextInt(set.size())));
                break;
            case ASESINO:
                noTeam = Arrays.asList(356, 495);
                real = Arrays.asList(521, 691);
                chaos = Arrays.asList(684, 701);
                set = noTeam;
//                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectSystem.getObject(set.get(random.nextInt(set.size())));
                break;
            case CLERIGO:
                noTeam = Arrays.asList(356, 495);
                real = Collections.singletonList(521);
                chaos = Collections.singletonList(523);
                set = noTeam;
//                set = team.equals(Team.NO_TEAM) ? noTeam : team.equals(Team.CAOS_ARMY) ? chaos : real;
                result = objectSystem.getObject(set.get(random.nextInt(set.size())));
                break;
        }
        return result;
    }

    private Optional<Obj> getHelmet(Hero hero) {
        Optional<Obj> result = Optional.empty();
        switch (hero) {
            case PALADIN:
            case GUERRERO:
                result = objectSystem.getObject(405);
                break;
            case ARQUERO:
                Random random = new Random();
                result = objectSystem.getObject(Arrays.asList(1052, 1003).get(random.nextInt(2)));
                break;
            case CLERIGO:
            case ASESINO:
                result = objectSystem.getObject(131);
                break;
            case BARDO:
            case MAGO:
                result = objectSystem.getObject(851);
                break;
            default:
                break;
        }
        return result;
    }

    private Optional<Obj> getShield(Hero hero) {
        Optional<Obj> result = Optional.empty();
        switch (hero) {
            case PALADIN:
            case CLERIGO:
            case GUERRERO:
//                int id = team.equals(Team.NO_TEAM) ? 130 : team.equals(Team.REAL_ARMY) ? 1038 : 1037;
                int id = 130;
                result = objectSystem.getObject(id);
                break;
            case BARDO:
            case ASESINO:
                result = objectSystem.getObject(404);
                break;
            default:
                break;
        }
        return result;
    }

    private Set<Obj> getWeapon(Hero hero) {
        Set<Obj> result = new HashSet<>();
        switch (hero) {
            case PALADIN:
            case GUERRERO:
                objectSystem.getObject(403).ifPresent(result::add);
                break;
            case ARQUERO:
                objectSystem.getObject(665).ifPresent(result::add);
                objectSystem.getObject(366).ifPresent(result::add);
                break;
            case BARDO:
                objectSystem.getObject(366).ifPresent(result::add);
                break;
            case CLERIGO:
                objectSystem.getObject(129).ifPresent(result::add);
                break;
            case ASESINO:
                objectSystem.getObject(559).ifPresent(result::add);
                break;
            case MAGO:
                objectSystem.getObject(660).ifPresent(result::add);
                break;
            default:
                break;
        }
        objectSystem.getObject(665).ifPresent(result::add);// arco
        return result;
    }

    private void setSpells(int entityId, Hero hero) {
        Set<Spell> spells = getSpells(hero);
        final Integer[] spellIds = spells
                .stream()
                .map(spell -> spellSystem.getId(spell))
                .toArray(Integer[]::new);
        mSpellBook.create(entityId).setSpells(spellIds);
    }

    private Set<Spell> getSpells(Hero hero) {
        final Map<Integer, Spell> spells = spellSystem.getSpells();
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

    private void setEntityPosition(int entityId) {
        WorldPos spot = new WorldPos(50, 50, 1);
        setWorldPosition(entityId, spot);
    }

    private void setWorldPosition(int entityId, WorldPos spot) {
        setWorldPosition(entityId, spot, false);
    }

    // @todo: Refactor la lógica de spawn (que pueda fallar)
    private void setWorldPosition(int entityId, WorldPos spot, boolean item) {
        shared.model.map.Map map = mapSystem.getHelper().getMap(spot.getMap());
        for (int i = 0; i < 12; i++) {
            Optional<WorldPos> candidate = rhombLegalPos(spot, i, map, item);
            if (candidate.isPresent()) {
                setPosition(entityId, candidate.get());
                break;
            }
        }

    }

    private void setPosition(int entityId, WorldPos targetPos) {
        WorldPos worldPos = mWorldPos.create(entityId);
        worldPos.map = targetPos.map;
        worldPos.x = targetPos.x;
        worldPos.y = targetPos.y;

        WorldPosOffsets worldPosOffsets = mWorldPosOffsets.create(entityId);
        worldPosOffsets.x = 0;
        worldPosOffsets.y = 0;
    }

    private Optional<WorldPos> rhombLegalPos(WorldPos spot, int i, shared.model.map.Map map, boolean item) {
        WorldPos newSpot = new WorldPos(spot);
        newSpot.x -= i;
        for (int j = 0; j < i; j++) {
            newSpot.x += j;
            newSpot.y -= j;
            if (isNotBusy(map, newSpot, item)) {
                return Optional.of(newSpot);
            }
        }

        newSpot = new WorldPos(spot);
        newSpot.y -= i;
        for (int j = 0; j < i; j++) {
            newSpot.x += j;
            newSpot.y += j;
            if (isNotBusy(map, newSpot, item)) {
                return Optional.of(newSpot);
            }
        }

        newSpot = new WorldPos(spot);
        newSpot.x += i;
        for (int j = 0; j < i; j++) {
            newSpot.x -= j;
            newSpot.y += j;
            if (isNotBusy(map, newSpot, item)) {
                return Optional.of(newSpot);
            }
        }

        newSpot = new WorldPos(spot);
        newSpot.y += i;
        for (int j = 0; j < i; j++) {
            newSpot.x -= j;
            newSpot.y -= j;
            if (isNotBusy(map, newSpot, item)) {
                return Optional.of(newSpot);
            }
        }

        return Optional.empty();
    }

    private boolean isNotBusy(shared.model.map.Map map, WorldPos newSpot, boolean item) {
        MapHelper helper = mapSystem.getHelper();
        if (helper.isBlocked(map, newSpot)) {
            return false;
        }
        boolean hasItem = (item && helper.isObjTileBusy(mapSystem.getEntities(newSpot), newSpot));
        boolean hasEntity = helper.hasEntity(mapSystem.getEntities(newSpot), newSpot);

        return !hasEntity && !hasItem;
    }
}
