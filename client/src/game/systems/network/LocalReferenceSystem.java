package game.systems.network;

import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import component.entity.Ref;
import game.systems.world.NetworkedEntitySystem;
import shared.systems.ReferenceSystem;

import static com.artemis.E.E;

@Wire
@All({Ref.class})
public class LocalReferenceSystem extends ReferenceSystem {

    private NetworkedEntitySystem networkedEntitySystem;

    @Override
    protected void inserted(int entityId) {
        int networkId = E(entityId).refId();
        if (networkedEntitySystem.exists(networkId)) {
            E(entityId).refId(networkedEntitySystem.getLocalId(networkId));
        } else {
            delete(entityId);
        }
    }

    @Override
    protected void delete(int entityId) {
        if (E(entityId).hasWorldPos()) {
            E(entityId).removeRef();
        } else {
            super.delete(entityId);
        }
    }
}
