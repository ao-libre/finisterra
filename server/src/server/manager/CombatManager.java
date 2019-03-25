package server.manager;

import server.database.model.attributes.Attributes;
import server.database.model.modifiers.Modifiers;
import shared.interfaces.CharClass;
import shared.interfaces.Hero;
import shared.model.Spell;
import shared.network.notifications.EntityUpdate;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.objects.types.WeaponObj;
import com.artemis.Component;
import com.artemis.E;
import entity.CombatMessage;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

/**
 *
 */
/*
 * All logic related to Combat: calculation of damage, evasion, magic, etc.
 */
public class CombatManager {

    public static final String MISS = "MISS";

    /**
     * Update the attacked entity in case it was damaged
     * @param attacker attacker entity id
     * @param attacked victim entity id
     * @return possible damage
     */
    public static Optional<Integer> attack(int attacker, int attacked) {
        Optional<Integer> damage = hit(attacker, attacked);
        if (damage.isPresent()) {
            E(attacked).getHealth().min -= damage.get();
            update(attacked);
        }
        return damage;
    }

    /**
     * @param attacker entity id
     * @param attacked entity id
     * @return Take into account evasion and if it hits then return damage
     */
    private static Optional<Integer> hit(int attacker, int attacked) {
        // calculate evasion
        float evasion = getEvasion(attacked);
        // hit??
        int prob = ThreadLocalRandom.current().nextInt(100);
        if (prob < evasion) {
            // calculate damage
            int damage = calculateDamage(attacker);
            // calculate defense
            int defense = calculateDefense(attacked, evasion);
            return Optional.of(damage - defense);
        }
        return Optional.empty();
    }


    /**
     * @param attacked entity id
     * @param evasion entity id
     * @return defanse of entity, taking into account evasion
     */
    private static int calculateDefense(int attacked, float evasion) {
        E entity = E(attacked);
        CharClass clazz = getCharClass(entity);
        float evasionModifier = Modifiers.EVASION.of(clazz);

        int defense = 1; // TODO complete

        return (int) (evasion * defense * evasionModifier);
    }


    /**
     * @param attacker entity id
     * @return damage that will cause
     */
    private static int calculateDamage(int attacker) {
        E entity = E(attacker);
        CharClass clazz = getCharClass(entity);
        int weaponDamage = 0;
        if (entity.hasWeapon()) {
            int weaponIndex = entity.getWeapon().index;
            Optional<Obj> object = ObjectManager.getObject(weaponIndex);
            if (object.isPresent() && object.get().getType().equals(Type.WEAPON)) {
                WeaponObj weapon = (WeaponObj) object.get();
                weaponDamage = ThreadLocalRandom.current().nextInt(weapon.getMinHit(), weapon.getMaxHit() + 1);
            }
        }
        return (int) Attributes.STRENGTH.of(clazz) * weaponDamage; // TODO calculate hero strength
    }


    /**
     * @param victimId entity id
     * @return evasion of entity class
     */
    private static float getEvasion(int victimId) {
        E victim = E(victimId);
        CharClass clazz = getCharClass(victim);
        return Attributes.EVASION.of(clazz) * 2; // TODO calculate extra evasion from items and hero modifier
    }


    /**
     * TODO move to another class
     * @param victim entity
     * @return class of current hero
     */
    private static CharClass getCharClass(E victim) {
        int heroId = victim.getCharHero().heroId;
        Hero hero = Hero.values()[heroId];
        return CharClass.values()[hero.getClassId()];
    }


    /**
     * Send combat notification to user and near by entities
     * @param victim entity id
     * @param combatMessage message
     */
    public static void notify(int victim, CombatMessage combatMessage) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{combatMessage}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
        WorldManager.notifyToNearEntities(victim, update);
    }

    /**
     * Send an update to entity with current health
     * @param victim entity id
     */
    static void update(int victim) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{E(victim).getHealth()}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
    }


    /**
     * @param target entity id
     * @param spell spell used
     * @return magical damage
     */
    static int calculateMagicDamage(int target, Spell spell) {
        // TODO complete
        if (spell.getSumHP() == 1) {
            return 80;
        }
        return -80;
    }
}
