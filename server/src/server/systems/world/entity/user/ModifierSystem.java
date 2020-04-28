package server.systems.world.entity.user;

import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.database.model.modifiers.Modifiers;
import server.systems.config.ConfigurationSystem;
import shared.interfaces.CharClass;

@Wire
public class ModifierSystem extends PassiveSystem {

    private ConfigurationSystem configurationSystem;

    public float of(Modifiers modifier, CharClass clazz) {
        return configurationSystem.getCharConfig().getCharClass(clazz)
                .getModifier().getValue(modifier);
    }
}
