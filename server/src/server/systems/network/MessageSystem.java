package server.systems.network;

import component.console.ConsoleMessage;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.utils.UpdateTo;
import shared.util.EntityUpdateBuilder;

public class MessageSystem extends PassiveSystem {

    private EntityUpdateSystem entityUpdateSystem;

    public MessageSystem() {
    }

    public void add(int entityID, ConsoleMessage message) {
        entityUpdateSystem.add(entityID, EntityUpdateBuilder.none().withComponents(message).build(), UpdateTo.ENTITY);
    }

}
