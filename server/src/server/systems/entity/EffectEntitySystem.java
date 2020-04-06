package server.systems.entity;

import com.artemis.annotations.Wire;
import component.graphic.Effect;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;

@Wire
public class EffectEntitySystem extends PassiveSystem {

    private EntityUpdateSystem entityUpdateSystem;

    private Map<Integer, Map<Integer, Integer>> entityEffects;
    private Map<Integer, Map<Integer, Integer>> entityFXs;

    public EffectEntitySystem() {
        entityEffects = new ConcurrentHashMap<>();
    }

    public void addFX(int entity, int fxNumber, int loops) {
        int effectEntityId = world.create();
        E(effectEntityId)
                .effectLoops(loops)
                .effectEffectId(fxNumber)
                .effectType(Effect.Type.FX);
        if (Effect.LOOP_INFINITE == loops) {
            entityFXs
                    .computeIfAbsent(entity, integer -> new ConcurrentHashMap<>())
                    .put(fxNumber, effectEntityId);
        } else {
            E(effectEntityId).clear();
        }
        entityUpdateSystem.attach(entity, effectEntityId);
    }

    public void addEffect(int entity, int particleNumber, int loops) {
        int effectEntityId = world.create();
        E(effectEntityId)
                .effectLoops(loops)
                .effectEffectId(particleNumber)
                .effectType(Effect.Type.PARTICLE);
        if (Effect.LOOP_INFINITE == loops) {
            entityEffects
                    .computeIfAbsent(entity, integer -> new ConcurrentHashMap<>())
                    .put(particleNumber, effectEntityId);
        } else {
            E(effectEntityId).clear();
        }
        entityUpdateSystem.attach(entity, effectEntityId);
    }

    public void removeEffect(int entity, int particleNumber) {
        entityEffects.computeIfPresent(entity, (id, map) -> {
            map.computeIfPresent(particleNumber, (effectID, effectEntity) -> {
                entityUpdateSystem.detach(entity, effectEntity);
                return effectEntity;
            });
            map.remove(particleNumber);
            return map;
        });
    }

    public void removeFX(int entity, int fxNumber) {
        entityFXs.computeIfPresent(entity, (id, map) -> {
            map.computeIfPresent(fxNumber, (effectID, effectEntity) -> {
                entityUpdateSystem.detach(entity, effectEntity);
                return effectEntity;
            });
            map.remove(fxNumber);
            return map;
        });
    }
}
