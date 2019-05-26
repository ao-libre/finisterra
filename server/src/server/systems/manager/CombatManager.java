package server.systems.manager;

import com.artemis.E;
import entity.world.CombatMessage;
import server.core.Server;
import server.database.model.modifiers.Modifiers;
import shared.interfaces.CharClass;
import shared.interfaces.Hero;
import shared.model.Spell;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.objects.types.WeaponObj;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

/**
 *
 */
/*
 * All logic related to Combat: calculation of damage, evasion, magic, etc.
 */
@Deprecated
public class CombatManager extends DefaultManager {


    public CombatManager(Server server) {
        super(server);
    }

    /**
     * TODO move to another class
     *
     * @param victim entity
     * @return class of current hero
     */
    public static CharClass getCharClass(E victim) {
        int heroId = victim.getCharHero().heroId;
        Hero hero = Hero.getHeroes().get(heroId);
        return CharClass.values()[hero.getClassId()];
    }

    /**
     * @param target entity id
     * @param spell  spell used
     * @return magical damage
     */
    static int calculateMagicDamage(int target, Spell spell) {
        // TODO complete
        if (spell.getSumHP() == 1) {
            return 80;
        }
        return -80;
    }

    /**
     * Update the attacked entity in case it was damaged
     *
     * @param attacker attacker entity id
     * @param attacked victim entity id
     * @return possible damage
     */
    public Optional<Integer> attack(int attacker, int attacked) {
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
    private Optional<Integer> hit(int attacker, int attacked) {
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
     * @param evasion  entity id
     * @return defanse of entity, taking into account evasion
     */
    private int calculateDefense(int attacked, float evasion) {
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
    private int calculateDamage(int attacker) {
        E entity = E(attacker);
        int weaponDamage = 0;
        if (entity.hasWeapon()) {
            int weaponIndex = entity.getWeapon().index;
            Optional<Obj> object = getServer().getObjectManager().getObject(weaponIndex);
            if (object.isPresent() && object.get().getType().equals(Type.WEAPON)) {
                WeaponObj weapon = (WeaponObj) object.get();
                weaponDamage = ThreadLocalRandom.current().nextInt(weapon.getMinHit(), weapon.getMaxHit() + 1);
            }
        }
        return entity.strengthCurrentValue() * weaponDamage;
    }

    /**
     * @param victimId entity id
     * @return evasion of entity class
     */
    private float getEvasion(int victimId) {
        E victim = E(victimId);
        CharClass clazz = getCharClass(victim);
        return 2; // TODO calculate extra evasion from items and hero modifier
    }

    /**
     * Send combat notification to user and near by entities
     *
     * @param victim        entity id
     * @param combatMessage message
     */
    public void notify(int victim, CombatMessage combatMessage) {
        EntityUpdate update = EntityUpdateBuilder.of(victim).withComponents(combatMessage).build();
        getServer().getWorldManager().sendEntityUpdate(victim, update);
        getServer().getWorldManager().notifyToNearEntities(victim, update);
    }

    /**
     * Send an update to entity with current health
     *
     * @param victim entity id
     */
    void update(int victim) {
        EntityUpdate update = EntityUpdateBuilder.of(victim).withComponents(E(victim).getHealth()).build();
        getServer().getWorldManager().sendEntityUpdate(victim, update);
    }

}
