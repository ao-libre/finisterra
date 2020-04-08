package server.systems.entity;

import com.artemis.annotations.Wire;
import component.entity.Ref;
import component.position.WorldPos;
import component.sound.AOSound;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.UpdateTo;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
public class SoundEntitySystem extends PassiveSystem {

    private EntityUpdateSystem entityUpdateSystem;

    private Map<Integer, Map<Integer, Integer>> entitySounds;

    public SoundEntitySystem() {
        entitySounds = new ConcurrentHashMap<>();
    }

    public void add(int entity, int soundNumber) {
        // add temporal sound
        EntityUpdateBuilder builder = EntityUpdateBuilder.none()
                .withComponents(new AOSound(soundNumber, false))
                .withComponents(new Ref(entity));
        if (E(entity).hasWorldPos()) {
            builder.withComponents(E(entity).getWorldPos());
        }
        EntityUpdate soundUpdate = builder.build();
        entityUpdateSystem.add(entity, soundUpdate, UpdateTo.ALL);
    }

    public void add(int entity, int soundNumber, boolean loop) {
        int soundEntity = world.create();
        E(soundEntity)
                .aOSoundId(soundNumber)
                .aOSoundShouldLoop(loop);

        if (E(entity).hasWorldPos()) {
            WorldPos worldPos = E(entity).getWorldPos();
            E(soundEntity)
                    .worldPosMap(worldPos.map)
                    .worldPosX(worldPos.x)
                    .worldPosY(worldPos.y);
        }
        entitySounds
                .computeIfAbsent(entity, integer -> new ConcurrentHashMap<>())
                .put(soundNumber, soundEntity);

        entityUpdateSystem.attach(entity, soundEntity);
    }

    public void remove(int entity, int soundNumber) {
        entitySounds.computeIfPresent(entity, (id, map) -> {
            map.computeIfPresent(soundNumber, (sID, sEntity) -> {
                entityUpdateSystem.detach(entity, sEntity);
                return sEntity;
            });
            map.remove(soundNumber);
            return map;
        });
    }
}
