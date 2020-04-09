package shared.systems;

import com.artemis.E;
import com.artemis.annotations.One;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.physics.AttackInterval;
import component.physics.UseInterval;

@Wire
@One({AttackInterval.class, UseInterval.class})
public class IntervalSystem extends IteratingSystem {

    @Override
    protected void process(int entityId) {
        // reduce interval component values
        E entity = E.E(entityId);
        if (entity.hasAttackInterval()) {
            entity.attackIntervalValue(entity.attackIntervalValue() - world.getDelta());
            if (entity.attackIntervalValue() <= 0) {
                entity.removeAttackInterval();
            }
        }
        if (entity.hasUseInterval()) {
            entity.useIntervalValue(entity.useIntervalValue() - world.getDelta());
            if (entity.useIntervalValue() <= 0) {
                entity.removeUseInterval();
            }
        }
    }

}
