package server.systems.entity;

import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;

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
        add(entity, soundNumber, false);
    }

    public void add(int entity, int soundNumber, boolean loop) {
        int soundEntity = world.create();
        // create Sound component
        E(soundEntity)
                .aOSoundId(soundNumber)
                .aOSoundShouldLoop(loop);
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
