package server.systems.entity;

import com.artemis.annotations.Wire;
import component.graphic.Effect;
import component.graphic.EffectBuilder;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.UpdateTo;
import shared.util.EntityUpdateBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.artemis.E.E;
import static shared.network.notifications.EntityUpdate.NO_ENTITY;

@Wire
public class EffectEntitySystem extends PassiveSystem {

    private EntityUpdateSystem entityUpdateSystem;

    private Map<Integer, Map<Integer, Integer>> entityEffects;
    private Map<Integer, Map<Integer, Integer>> entityFXs;

    public EffectEntitySystem() {
        entityEffects = new ConcurrentHashMap<>();
    }

    public void addFX(int entity, int fxNumber, int loops) {
        Effect effect = EffectBuilder
                .create()
                .withFX(fxNumber)
                .withLoops(loops)
                .build();
        int effectEntityId = NO_ENTITY;
        if (Effect.LOOP_INFINITE == loops) {
            // create entity and save
            effectEntityId = world.create();
            entityFXs
                    .computeIfAbsent(entity, integer -> new ConcurrentHashMap<>())
                    .put(fxNumber, effectEntityId);
        }

        EntityUpdateBuilder of = EntityUpdateBuilder.of(effectEntityId);
        of.withComponents(effect);
        if (E(entity).hasWorldPos()) {
            of.withComponents(E(entity).getWorldPos());
        }
        entityUpdateSystem.add(entity, of.build(), UpdateTo.ALL);
    }

    public void addEffect(int entity, int particleNumber, int loops) {
        int effectEntityId = world.create();
        E(effectEntityId)
                .effectLoops(loops)
                .effectEffectId(particleNumber)
                .effectType(Effect.Type.PARTICLE);

        doAdd(entity, particleNumber, loops, effectEntityId, entityEffects);
    }

    private void doAdd(int entity, int fxNumber, int loops, int effectEntityId, Map<Integer, Map<Integer, Integer>> entityFXs) {
        if (E(entity).hasWorldPos()) {
            // copy worldPos to effect
            WorldPos worldPos = E(entity).getWorldPos();
            E(effectEntityId)
                    .worldPosMap(worldPos.map)
                    .worldPosX(worldPos.x)
                    .worldPosY(worldPos.y);
        }
        if (Effect.LOOP_INFINITE == loops) {
            entityFXs
                    .computeIfAbsent(entity, integer -> new ConcurrentHashMap<>())
                    .put(fxNumber, effectEntityId);
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
