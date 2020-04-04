package game.systems;

import component.camera.Focused;
import com.artemis.E;
import com.artemis.annotations.Wire;
import game.systems.network.ClientSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import component.position.WorldPos;

@Wire
public class PlayerSystem extends PassiveSystem {

    private ClientSystem clientSystem;

    public E get() {
        return E.withComponent(Focused.class).iterator().next();
    }

    public WorldPos getWorldPos() {
        return get().getWorldPos();
    }

}
