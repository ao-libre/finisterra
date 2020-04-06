package game.systems.actions;

import com.artemis.E;
import com.artemis.annotations.All;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import component.camera.Focused;
import component.physics.AttackInterval;
import component.physics.UseInterval;
import game.systems.PlayerSystem;

@All({Focused.class})
@One({AttackInterval.class, UseInterval.class})
public class IntervalSystem extends IteratingSystem {

    private PlayerSystem playerSystem;

    @Override
    protected void process(int entityId) {
        // reduce interval component values
        E player = playerSystem.get();
        if (player.hasAttackInterval()) {
            player.attackIntervalValue(player.attackIntervalValue() - world.getDelta());
            if (player.attackIntervalValue() <= 0) {
                player.removeAttackInterval();
            }
        }
        if (player.hasUseInterval()) {
            player.useIntervalValue(player.useIntervalValue() - world.getDelta());
            if (player.useIntervalValue() <= 0) {
                player.removeUseInterval();
            }
        }
    }

    public boolean canSpellAttack() {
        E player = playerSystem.get();
        return !player.hasAttackInterval() && !player.isMeditating();
    }

    public boolean canPhysicAttack() {
        E player = playerSystem.get();
        return !player.hasAttackInterval() && !player.isMeditating();
    }

    public boolean canUse() {
        E player = playerSystem.get();
        return !player.hasUseInterval() && !player.hasAttackInterval() && !player.isMeditating();
    }
}
