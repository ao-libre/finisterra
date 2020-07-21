package game.systems;

import com.artemis.E;
import com.artemis.annotations.Wire;
import component.camera.Focused;
import component.position.WorldPos;
import game.systems.network.ClientSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class PlayerSystem extends PassiveSystem {

    private ClientSystem clientSystem;

    public E get() {
        return E.withComponent(Focused.class).iterator().next();
    }

    public WorldPos getWorldPos() {
        return get().getWorldPos();
    }

    public boolean canSpellAttack() {
        E player = get();
        return !player.hasAttackInterval() && !player.isMeditating();
    }

    public boolean canPhysicAttack() {
        E player = get();
        return !player.hasAttackInterval() && !player.isMeditating();
    }

    public boolean canUse() {
        E player = get();
        return !player.hasUseInterval() && !player.hasAttackInterval() && !player.isMeditating() && (player.healthMin() >= 1);
    }
}
