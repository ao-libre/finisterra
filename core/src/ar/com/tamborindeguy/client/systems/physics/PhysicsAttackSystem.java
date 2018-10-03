package ar.com.tamborindeguy.client.systems.physics;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import physics.Attack;

import static com.artemis.E.E;

public class PhysicsAttackSystem extends IteratingSystem {

    public PhysicsAttackSystem() {
        super(Aspect.all(Attack.class));
    }

    @Override
    protected void process(int entityId) {
        Attack attack = E(entityId).getAttack();
        attack.interval -= world.getDelta();
        if (attack.interval <= 0) {
            E(entityId).removeAttack();
        }
    }
}
