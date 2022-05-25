package server.systems.world.entity.factory;

import com.artemis.ComponentMapper;
import component.entity.Clear;
import component.graphic.Effect;
import component.graphic.EffectBuilder;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.network.EntityUpdateSystem;
import server.utils.UpdateTo;
import shared.util.EntityUpdateBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static shared.network.notifications.EntityUpdate.NO_ENTITY;

public class EffectEntitySystem extends PassiveSystem {

    private EntityUpdateSystem entityUpdateSystem;

    private Map<Integer, Map<Integer, Integer>> entityEffects;
    private Map<Integer, Map<Integer, Integer>> entityFXs;

    ComponentMapper<Clear> mClear;
    ComponentMapper<Effect> mEffect;
    ComponentMapper<WorldPos> mWorldPos;

    public EffectEntitySystem() {
        entityEffects = new ConcurrentHashMap<>();
    }

    public void addFX(int entityId, int fxNumber, int loops) {
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
                    .computeIfAbsent(entityId, integer -> new ConcurrentHashMap<>())
                    .put(fxNumber, effectEntityId);
        }

        EntityUpdateBuilder of = EntityUpdateBuilder.of(effectEntityId);
        of.withComponents(effect);
        if (mWorldPos.has(entityId)) {
            of.withComponents(mWorldPos.get(entityId));
        }
        entityUpdateSystem.add(entityId, of.build(), UpdateTo.ALL);
    }

    public void addEffect(int entity, int particleNumber, int loops) {
        int effectEntityId = world.create();
        Effect effect = mEffect.create(effectEntityId);
        effect.setLoops(loops);
        effect.setEffectId(particleNumber);
        effect.setType(Effect.Type.PARTICLE);

        doAdd(entity, particleNumber, loops, effectEntityId, entityEffects);
    }

    private void doAdd(int entityId, int fxNumber, int loops, int effectEntityId, Map<Integer, Map<Integer, Integer>> entityFXs) {
        if (mWorldPos.has(entityId)) {
            // copy worldPos to effect
            WorldPos worldPos = mWorldPos.get(entityId);
            mWorldPos.create(effectEntityId).setWorldPos(worldPos);
        }
        if (Effect.LOOP_INFINITE == loops) {
            entityFXs
                .computeIfAbsent(entityId, integer -> new ConcurrentHashMap<>())
                .put(fxNumber, effectEntityId);
        } else {
            mClear.create(effectEntityId);
        }
        entityUpdateSystem.attach(entityId, effectEntityId);
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
