package server.systems.world.entity.ai;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import component.entity.npc.Respawn;
import server.systems.world.entity.factory.EntityFactorySystem;

public class RespawnSystem extends IteratingSystem {

    private EntityFactorySystem entityFactorySystem;

    ComponentMapper<Respawn> mRespawn;

    public RespawnSystem() {
        super(Aspect.all(Respawn.class));
    }

    @Override
    protected void process(int entityId) {
        Respawn respawn = mRespawn.get(entityId);
        respawn.setTime(respawn.getTime() - world.getDelta());
        if (respawn.getTime() <= 0) {
            entityFactorySystem.createNPC(respawn.getNpcId(), respawn.getPos().toWorldPos());
            world.delete(entityId);
        }
    }
}
