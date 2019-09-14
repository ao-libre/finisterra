package game.systems.physics;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import physics.AttackInterval;
import physics.AttackAnimation;

import static com.artemis.E.E;

public class PhysicsAttackSystem extends IteratingSystem {

    public PhysicsAttackSystem() {
        super(Aspect.one(AttackAnimation.class, AttackInterval.class));
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        if (entity.hasAttack()) {
            AttackInterval attack = entity.getAttackInterval();
            attack.interval -= world.getDelta();
            if (attack.interval <= 0) {
                entity.removeAttack();
            }
        }
        if (entity.hasAttackAnimation()) {
            AttackAnimation attackAnimation = entity.getAttackAnimation();
            attackAnimation.time -= world.getDelta();
            if (attackAnimation.time <= 0) {
                entity.removeAttackAnimation();
            }
        }
    }
}
