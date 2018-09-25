package ar.com.tamborindeguy.client.systems.physics;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import entity.Heading;
import physics.PhysicAttack;
import position.WorldPos;

import static com.artemis.E.E;

public class PhysicsAttackSystem extends IteratingSystem {

    public PhysicsAttackSystem() {
        super(Aspect.all(WorldPos.class, PhysicAttack.class, Heading.class));
    }

    @Override
    protected void process(int entityId) {
        WorldPos worldPos = E(entityId).getWorldPos();
        WorldPos attackedPos = worldPos.getNextPos(E(entityId).getHeading());
        if (isAttackable(attackedPos)) {
            // do attack
        }
    }

    private boolean isAttackable(WorldPos attackedPos) {
        return true;
    }
}
