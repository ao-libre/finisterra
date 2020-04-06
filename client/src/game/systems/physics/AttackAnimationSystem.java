package game.systems.physics;

import com.artemis.E;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import component.physics.AttackAnimation;

import static com.artemis.E.E;

@All(AttackAnimation.class)
public class AttackAnimationSystem extends IteratingSystem {

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        AttackAnimation attackAnimation = entity.getAttackAnimation();
        attackAnimation.time -= world.getDelta();
        if (attackAnimation.time <= 0) {
            entity.removeAttackAnimation();
        }
    }
}
