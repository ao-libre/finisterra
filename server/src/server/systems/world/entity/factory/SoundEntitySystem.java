package server.systems.world.entity.factory;

import com.artemis.ComponentMapper;
import component.entity.Ref;
import component.position.WorldPos;
import component.sound.AOSound;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.utils.UpdateTo;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundEntitySystem extends PassiveSystem {

    private EntityUpdateSystem entityUpdateSystem;

    private Map<Integer, Map<Integer, Integer>> entitySounds;

    ComponentMapper<WorldPos> mWorldPos;
    ComponentMapper<AOSound> mAOSound;

    public SoundEntitySystem() {
        entitySounds = new ConcurrentHashMap<>();
    }

    public void add(int entityId, int soundNumber) {
        // add temporal sound
        EntityUpdateBuilder builder = EntityUpdateBuilder.none()
                .withComponents(new AOSound(soundNumber, false))
                .withComponents(new Ref(entityId));
        if (mWorldPos.has(entityId)) {
            builder.withComponents(mWorldPos.get(entityId));
        }
        EntityUpdate soundUpdate = builder.build();
        entityUpdateSystem.add(entityId, soundUpdate, UpdateTo.ALL);
    }

    public void add(int entityId, int soundNumber, boolean loop) {
        int soundEntityId = world.create();
        mAOSound.create(soundEntityId).setId(soundNumber);
        mAOSound.get(soundEntityId).setShouldLoop(loop);

        if (mWorldPos.has(entityId)) {
            WorldPos worldPos = mWorldPos.get(entityId);
            mWorldPos.create(soundEntityId).setWorldPos(worldPos);
        }
        entitySounds
                .computeIfAbsent(entityId, integer -> new ConcurrentHashMap<>())
                .put(soundNumber, soundEntityId);

        entityUpdateSystem.attach(entityId, soundEntityId);
    }

    public void remove(int entityId, int soundNumber) {
        entitySounds.computeIfPresent(entityId, (id, map) -> {
            map.computeIfPresent(soundNumber, (sID, sEntity) -> {
                entityUpdateSystem.detach(entityId, sEntity);
                return sEntity;
            });
            map.remove(soundNumber);
            return map;
        });
    }
}
