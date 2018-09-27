package ar.com.tamborindeguy.manager;

import com.artemis.Component;
import entity.CombatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static void notify(int victim, String attack) {
        List<Component> components = new ArrayList<>();
        components.add(new CombatMessage(attack));
        WorldManager.sendEntityUpdate(victim, victim, components);
        WorldManager.notifyUpdateToNearEntities(victim, components);
    }

    public static void update(int victim) {
        List<Component> components = new ArrayList<>();
        components.add(E(victim).getHealth());
        WorldManager.sendEntityUpdate(victim, victim, components);
    }
}
