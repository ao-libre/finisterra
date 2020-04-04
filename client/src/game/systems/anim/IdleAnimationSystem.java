package game.systems.anim;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.parts.Body;
import component.entity.character.states.Heading;
import component.entity.character.status.Health;
import game.systems.resources.AnimationsSystem;
import model.textures.BundledAnimation;
import component.movement.Moving;
import component.physics.AttackAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.artemis.E.E;

public class IdleAnimationSystem extends IteratingSystem {

    private AnimationsSystem animationsSystem;

    public IdleAnimationSystem() {
        super(Aspect.all(Heading.class).exclude(Moving.class, AttackAnimation.class));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        // reset animation time
        E entity = E(entityId);
        final Heading heading = entity.getHeading();
        if (!entity.movementHasMovements()) {
            updateIdleTime(entity, heading, true);
        }
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        final Heading heading = entity.getHeading();
        updateIdleTime(entity, heading, false);
    }

    private void updateIdleTime(E entity, Heading heading, boolean reset) {
        List<BundledAnimation> animations = new ArrayList<>();
        if (entity.hasBody()) {
            final Body body = entity.getBody();
            animations.add(animationsSystem.getBodyAnimation(body, heading.current));
        }
        if (entity.hasWeapon()) {
            final Weapon weapon = entity.getWeapon();
            animations.add(animationsSystem.getWeaponAnimation(weapon, heading.current));
        }
        if (entity.hasShield()) {
            final Shield weapon = entity.getShield();
            animations.add(animationsSystem.getShieldAnimation(weapon, heading.current));
        }
        animations.stream().filter(Objects::nonNull).forEach(animation -> {
            float delta = world.getDelta();
            if (entity.hasHealth()) {
                Health health = entity.getHealth();
                if (health.min < health.max * 0.5f) {
                    delta *= 2;
                }
            }
            animation.addDeltaIdleTime(!reset ? delta : 0);
        });
    }

}
