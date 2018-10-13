package ar.com.tamborindeguy.client.systems.physics;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import physics.Attack;
import physics.AttackAnimation;

import static com.artemis.E.E;

public class PhysicsAttackSystem extends IteratingSystem {

    public PhysicsAttackSystem() {
        super(Aspect.one(AttackAnimation.class, Attack.class));
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        if (entity.hasAttack()) {
            Attack attack = entity.getAttack();
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
