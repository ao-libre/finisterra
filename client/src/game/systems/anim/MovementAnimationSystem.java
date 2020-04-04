package game.systems.anim;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.parts.Body;
import component.entity.character.states.Heading;
import game.systems.resources.AnimationsSystem;
import model.textures.BundledAnimation;
import component.movement.Moving;
import component.physics.AttackAnimation;
import shared.model.map.Tile;

import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class MovementAnimationSystem extends IteratingSystem {

    private AnimationsSystem animationsSystem;

    public MovementAnimationSystem() {
        super(Aspect.all(Heading.class).one(
                Moving.class, AttackAnimation.class));
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        // reset animation time
        E entity = E(entityId);
        final Heading heading = entity.getHeading();
        if (!entity.movementHasMovements()) {
            updateAnimationTime(entity, heading, true);
        }
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        final Heading heading = entity.getHeading();
        updateAnimationTime(entity, heading, false);
    }

    private void updateAnimationTime(E entity, Heading heading, boolean reset) {
        Optional<Float> velocity = Optional.empty();
        if (entity.hasAOPhysics() && entity.hasCharacter()) {
            velocity = Optional.of(entity.getAOPhysics().velocity);
        }
        if (entity.hasBody()) {
            final Body body = entity.getBody();
            BundledAnimation animation = animationsSystem.getBodyAnimation(body, heading.current);
            if (animation != null) {
                velocity.ifPresent(v -> animation.setFrameDuration(v / Tile.TILE_PIXEL_WIDTH));
                animation.setAnimationTime(!reset ? animation.getAnimationTime() + world.getDelta() : 0);
            }
        }
        if (entity.hasWeapon()) {
            final Weapon weapon = entity.getWeapon();
            BundledAnimation animation = animationsSystem.getWeaponAnimation(weapon, heading.current);
            if (animation != null) {
                velocity.ifPresent(v -> animation.setFrameDuration(v / Tile.TILE_PIXEL_WIDTH));
                animation.setAnimationTime(!reset ? animation.getAnimationTime() + world.getDelta() : 0);
            }
        }
        if (entity.hasShield()) {
            final Shield weapon = entity.getShield();
            BundledAnimation animation = animationsSystem.getShieldAnimation(weapon, heading.current);
            if (animation != null) {
                velocity.ifPresent(v -> animation.setFrameDuration(v / Tile.TILE_PIXEL_WIDTH));
                animation.setAnimationTime(!reset ? animation.getAnimationTime() + world.getDelta() : 0);
            }
        }
    }

}
