package server.systems.network;

import com.artemis.BaseSystem;
import com.google.common.collect.Sets;
import component.console.ConsoleMessage;
import shared.util.EntityUpdateBuilder;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

public class MessageSystem extends BaseSystem {

    private Map<Integer, Set<Integer>> messages;
    private EntityUpdateSystem entityUpdateSystem;


    public MessageSystem() {
        messages = new ConcurrentHashMap<>();
    }

    public void add(int entity, ConsoleMessage message) {
        int messageEntity;
        messages.computeIfAbsent(entity, id -> Sets.newHashSet()).add(messageEntity = world.create());
        entityUpdateSystem.add(entity, EntityUpdateBuilder.of(messageEntity).withComponents(message).build(), UpdateTo.ENTITY);
    }

    @Override
    protected void processSystem() {
        // when finishes
        messages.forEach((key, messages) -> messages.forEach(value -> E(value).clear()));
        // clear messages queue after all system processing
        messages.clear();
    }
}
