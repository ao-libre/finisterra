package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import com.artemis.Component;
import com.artemis.E;
import entity.CombatMessage;

import java.util.*;

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

    public static void notify(int victim, String attack) {
        E entity = E(victim);
        entity.combatMessage();
        entity.getCombatMessage().text = attack;
        EntityUpdate update = new EntityUpdate(victim, new Component[] {entity.getCombatMessage()}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
        WorldManager.notifyUpdateToNearEntities(update);
    }

    public static void update(int victim) {
        EntityUpdate update = new EntityUpdate(victim, new Component[] {E(victim).getHealth()}, new Class[0]);
        WorldManager.sendEntityUpdate(victim, update);
    }
}
