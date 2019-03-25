package game.systems.anim;

import model.textures.BundledAnimation;
import game.handlers.AnimationHandler;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import entity.Body;
import entity.Heading;
import entity.Shield;
import entity.Weapon;
import entity.character.Character;
import movement.Moving;
import physics.AttackAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.artemis.E.E;

public class IdleAnimationSystem extends IteratingSystem {

    public IdleAnimationSystem() {
        super(Aspect.all(Character.class, Heading.class).exclude(Moving.class, AttackAnimation.class));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        // reset animation time
        E entity = E(entityId);
        final Heading heading = entity.getHeading();
        if(!entity.movementHasMovements()) {
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
            animations.add(AnimationHandler.getBodyAnimation(body, heading.current));
        }
        if (entity.hasWeapon()) {
            final Weapon weapon = entity.getWeapon();
            animations.add(AnimationHandler.getWeaponAnimation(weapon, heading.current));
        }
        if (entity.hasShield()) {
            final Shield weapon = entity.getShield();
            animations.add(AnimationHandler.getShieldAnimation(weapon, heading.current));
        }
        animations.stream().filter(Objects::nonNull).forEach(animation -> animation.addDeltaIdleTime(!reset ? world.getDelta() : 0));
    }

}
