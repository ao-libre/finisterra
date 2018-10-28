package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import com.artemis.Component;
import entity.CombatMessage;

import java.util.Optional;

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
        // TODO implement
        return Optional.of(80);
    }

    public static void notify(int victim, CombatMessage combatMessage) {
        EntityUpdate update = new EntityUpdate(victim, new Component[] {combatMessage}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
        WorldManager.notifyToNearEntities(victim, update);
    }

    public static void update(int victim) {
        EntityUpdate update = new EntityUpdate(victim, new Component[] {E(victim).getHealth()}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
    }

    public static int calculateHP(int target, Spell spell) {
        if (spell.getSumHP() == 1) {
            return 80;
        }
        return - 80;
    }
}
