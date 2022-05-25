package game.systems.anim;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.parts.Body;
import component.entity.character.states.Heading;
import component.movement.Moving;
import component.physics.AttackAnimation;
import game.systems.resources.AnimationsSystem;
import model.textures.BundledAnimation;
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
        E entity = E(entityId);
        entity.removeCharAnimation();
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        if (entity.hasAOPhysics() && entity.hasCharacter()) {
            entity.charAnimationDuration(0.5f);
        }
        entity.charAnimationAdd(world.getDelta());
    }

}
