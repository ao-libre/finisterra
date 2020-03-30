package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.character.parts.Body;
import game.systems.resources.AnimationsSystem;
import game.systems.resources.DescriptorsSystem;
import game.systems.resources.ParticlesSystem;
import game.systems.world.NetworkedEntitySystem;
import game.systems.render.BatchRenderingSystem;
import game.utils.Pos2D;
import graphics.Effect;
import model.descriptors.BodyDescriptor;
import model.descriptors.FXDescriptor;
import model.textures.BundledAnimation;
import position.WorldPos;
import position.WorldPosOffsets;
import shared.model.map.Tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.artemis.E.E;
import static graphics.Effect.NO_REF;

@Wire
public class EffectRenderingSystem extends FluidIteratingSystem {

    private final Map<Integer, BundledAnimation> fxs;
    private final Map<Integer, ParticleEffect> particleEffects;

    private NetworkedEntitySystem networkedEntitySystem;
    private DescriptorsSystem descriptorsSystem;
    private AnimationsSystem animationsSystem;
    private BatchRenderingSystem batchRenderingSystem;

    private int srcFunc;
    private int dstFunc;

    public EffectRenderingSystem() {
        super(Aspect.all(Effect.class));
        this.particleEffects = new HashMap<>();
        this.fxs = new HashMap<>();
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        E e = E(entityId);
        if (e != null && e.hasEffect()) {
            Effect effect = e.getEffect();
            int effectId = effect.effectId;
            switch (effect.type) {
                case PARTICLE:
                    ParticleEffect particle = ParticlesSystem.getParticle(effectId);
                    particle.flipY();
                    particleEffects.put(entityId, particle);
                    break;
                case FX:
                    FXDescriptor fxDescriptor = descriptorsSystem.getFX(effectId);
                    BundledAnimation bundledAnimation = animationsSystem.getFX(effect);
                    fxs.put(entityId, bundledAnimation);
                    break;
            }
        }
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        fxs.remove(entityId);
        particleEffects.remove(entityId);
    }

    private void doBegin() {

        batchRenderingSystem.addTask((batch) -> {
                    srcFunc = batch.getBlendSrcFunc();
                    dstFunc = batch.getBlendDstFunc();
                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
                }
        );

    }

    private void doEnd() {
        batchRenderingSystem.addTask((batch) -> {
                    batch.setBlendFunction(srcFunc, dstFunc);
                }
        );
    }

    void drawEffect(E e, Optional<WorldPos> forcePos) {
        E candidate = e;
        if (e.hasEffect()) {
            Effect effect = e.getEffect();
            if (effect.entityReference != NO_REF) {
                int networkedEntity = effect.entityReference;
                if (networkedEntitySystem.exists(networkedEntity)) {
                    int entityId = networkedEntitySystem.get(networkedEntity);
                    E entity = E(entityId);
                    if (entity != null) {
                        candidate = entity;
                    }
                }
            }
            drawEffect(e, forcePos.orElse(candidate.getWorldPos()), candidate.getWorldPosOffsets());
        }
    }

    private void drawEffect(E e, WorldPos pos, WorldPosOffsets offsets) {
        doBegin();
        Pos2D screenPos = Pos2D.get(pos, offsets).toScreen();
        Effect effect = e.getEffect();
        int entityId = e.id();
        switch (effect.type) {
            case FX:
                BundledAnimation anim = fxs.get(entityId);
                int effectId = effect.effectId;
                FXDescriptor fxDescriptor = descriptorsSystem.getFX(effectId);
                TextureRegion graphic = anim.getGraphic();
                batchRenderingSystem.addTask((batch) ->
                        {
                            float x = screenPos.x + (Tile.TILE_PIXEL_WIDTH - graphic.getRegionWidth()) / 2 + fxDescriptor.getOffsetX();
                            float y = screenPos.y - graphic.getRegionHeight() + 20 + fxDescriptor.getOffsetY();
                            batch.draw(graphic, x, y);
                        }
                );
                break;
            case PARTICLE:
                ParticleEffect particleEffect = particleEffects.get(entityId);
                float x = particleEffect.getBoundingBox().getWidth();
                particleEffect.setPosition(screenPos.x + Tile.TILE_PIXEL_WIDTH / 2, screenPos.y);
                batchRenderingSystem.addTask((batch) ->
                        particleEffect.draw(batch, world.getDelta())
                );
                break;
        }
        doEnd();
    }

    private int getBodyOffset(int entityId) {
        int headOffsetY = 0;
        if (E(entityId).hasBody()) {
            final Body body = E(entityId).getBody();
            BodyDescriptor bodyDescriptor = descriptorsSystem.getBody(body.index);
            headOffsetY = Math.max(0, bodyDescriptor.getHeadOffsetY());
        }
        return headOffsetY;
    }

    @Override
    protected void process(E e) {
        Effect effect = e.getEffect();
        int id = e.id();
        switch (effect.type) {
            case FX:
                if (fxs.containsKey(id)) {
                    BundledAnimation anim = fxs.get(id);
                    if (anim.isAnimationFinished()) {
                        networkedEntitySystem.getNetworkedId(id).ifPresent(networkedEntitySystem::unregisterEntity);
                    } else {
                        anim.setAnimationTime(anim.getAnimationTime() + getWorld().getDelta() * (anim.getAnimation().getKeyFrames().length * 0.33f));
                    }
                }
                break;
            case PARTICLE:
                if (particleEffects.containsKey(id)) {
                    ParticleEffect particleEffect = particleEffects.get(id);
                    if (particleEffect.isComplete()) {
                        particleEffect.dispose();
                        e.deleteFromWorld();
                    }
                }
                break;
        }
    }
}
