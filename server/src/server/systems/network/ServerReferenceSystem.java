package server.systems.network;

import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.google.common.collect.Sets;
import component.entity.Ref;
import shared.systems.ReferenceSystem;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
@All({Ref.class})
public class ServerReferenceSystem extends ReferenceSystem {

    private Map<Integer, Set<Integer>> references;
    private Map<Integer, Integer> referenceTo;

    public ServerReferenceSystem() {
        references = new ConcurrentHashMap<>();
        referenceTo = new ConcurrentHashMap<>();
    }

    @Override
    protected void inserted(int entityId) {
        // keep relation between entities
        int refId = E(entityId).refId();
        referenceTo.put(entityId, refId);
        references.computeIfAbsent(refId, (id) -> Sets.newHashSet()).add(entityId);
    }

    @Override
    protected void delete(int entityId) {
        super.delete(entityId);
        Integer refId = referenceTo.get(entityId);
        // should be present
        references.get(refId).remove(entityId);
    }

    public Optional<Set<Integer>> getReferencesTo(int entityId) {
        return Optional.ofNullable(references.get(entityId));
    }
}
