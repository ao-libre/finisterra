package server.systems.network;

import component.console.ConsoleMessage;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.util.EntityUpdateBuilder;

public class MessageSystem extends PassiveSystem {

    private EntityUpdateSystem entityUpdateSystem;

    public MessageSystem() {}

    public void add(int entity, ConsoleMessage message) {
        entityUpdateSystem.add(entity, EntityUpdateBuilder.none().withComponents(message).build(), UpdateTo.ENTITY);
    }

}
