package server.systems.network;

import com.artemis.BaseSystem;
import component.console.ConsoleMessage;
import shared.util.EntityUpdateBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

public class MessageSystem extends BaseSystem {

    private Map<Integer, Integer> messages;
    private EntityUpdateSystem entityUpdateSystem;


    public MessageSystem() {
        messages = new ConcurrentHashMap<>();
    }

    public void add(int entity, ConsoleMessage message) {
        Integer messageEntity = messages.computeIfAbsent(entity, id -> world.create());
        entityUpdateSystem.add(entity, EntityUpdateBuilder.of(messageEntity).withComponents(message).build(), UpdateTo.ENTITY);
    }

    @Override
    protected void processSystem() {
        // when finishes
        messages.forEach((key, value) -> E(value).clear());
        // clear messages queue after all system processing
        messages.clear();
    }
}
