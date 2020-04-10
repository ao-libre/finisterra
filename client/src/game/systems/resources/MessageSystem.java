package game.systems.resources;

import com.artemis.annotations.Wire;
import game.handlers.DefaultAOAssetManager;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.util.Messages;

@Wire
public class MessageSystem extends PassiveSystem {
    @Wire
    private DefaultAOAssetManager aoAssetManager;

    public MessageSystem() {}

    public String getMessage(Messages key, String... params) {
        return aoAssetManager.getMessages(key, params);
    }
}
