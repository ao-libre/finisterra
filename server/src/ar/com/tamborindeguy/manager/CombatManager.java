package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.database.model.attributes.Attributes;
import ar.com.tamborindeguy.database.model.modifiers.Modifiers;
import ar.com.tamborindeguy.interfaces.CharClass;
import ar.com.tamborindeguy.interfaces.Hero;
import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import ar.com.tamborindeguy.objects.types.WeaponObj;
import com.artemis.Component;
import com.artemis.E;
import entity.CombatMessage;
import entity.Weapon;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

public class CombatManager {

    public static final String MISS = "MISS";

    public static Optional<Integer> attack(int attacker, int attacked) {
        Optional<Integer> damage = hit(attacker, attacked);
        if (damage.isPresent()) {
            E(attacked).getHealth().min -= damage.get();
            update(attacked);
        }
        return damage;
    }

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

    private static int calculateDefense(int attacked, float evasion) {
        E entity = E(attacked);
        CharClass clazz = getCharClass(entity);
        float evasionModifier = Modifiers.EVASION.of(clazz);

        int defense = 1; // TODO complete

        return (int) (evasion * defense * evasionModifier);
    }

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

    private static float getEvasion(int victimId) {
        E victim = E(victimId);
        CharClass clazz = getCharClass(victim);
        return Attributes.EVASION.of(clazz) * 2; // TODO calculate extra evasion from items and hero modifier
    }

    private static CharClass getCharClass(E victim) {
        int heroId = victim.getCharHero().heroId;
        Hero hero = Hero.values()[heroId];
        return CharClass.values()[hero.getClassId()];
    }

    public static void notify(int victim, CombatMessage combatMessage) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{combatMessage}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
        WorldManager.notifyToNearEntities(victim, update);
    }

    public static void update(int victim) {
        EntityUpdate update = new EntityUpdate(victim, new Component[]{E(victim).getHealth()}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
    }

    public static int calculateMagicDamage(int target, Spell spell) {
        // TODO complete
        if (spell.getSumHP() == 1) {
            return 80;
        }
        return -80;
    }
}
