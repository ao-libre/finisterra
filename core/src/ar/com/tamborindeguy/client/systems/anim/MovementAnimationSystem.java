package ar.com.tamborindeguy.client.systems.anim;

import ar.com.tamborindeguy.client.handlers.AnimationHandler;
import ar.com.tamborindeguy.model.textures.BundledAnimation;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.esotericsoftware.minlog.Log;
import entity.Body;
import entity.Heading;
import entity.Shield;
import entity.Weapon;
import entity.character.Character;
import movement.Moving;
import physics.Attack;
import physics.AttackAnimation;

import static com.artemis.E.E;

@Wire
public class MovementAnimationSystem extends IteratingSystem {

    public MovementAnimationSystem() {
        super(Aspect.all(Character.class, Heading.class).one(
                Moving.class, AttackAnimation.class));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        // reset animation time
        E entity = E(entityId);
        final Heading heading = entity.getHeading();
        updateAnimationTime(entity, heading, true);
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        final Heading heading = entity.getHeading();
        updateAnimationTime(entity, heading, false);
    }

    private void updateAnimationTime(E entity, Heading heading, boolean reset) {
        if (entity.hasBody()) {
            final Body body = entity.getBody();
            BundledAnimation animation = AnimationHandler.getBodyAnimation(body.index, heading.current);
            if (animation != null) {
                animation.setAnimationTime(!reset ? animation.getAnimationTime() + world.getDelta() : 0);
            }
        }
        if (entity.hasWeapon()) {
            final Weapon weapon = entity.getWeapon();
            BundledAnimation animation = AnimationHandler.getWeaponAnimation(weapon.index, heading.current);
            if (animation != null) {
                animation.setAnimationTime(!reset ? animation.getAnimationTime() + world.getDelta() : 0);
            }
        }
        if (entity.hasShield()) {
            final Shield weapon = entity.getShield();
            BundledAnimation animation = AnimationHandler.getShieldAnimation(weapon.index, heading.current);
            if (animation != null) {
                animation.setAnimationTime(!reset ? animation.getAnimationTime() + world.getDelta() : 0);
            }
        }
    }

}
