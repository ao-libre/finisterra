package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.character.parts.Body;
import game.handlers.DescriptorHandler;
import game.handlers.ParticlesHandler;
import game.managers.WorldManager;
import game.systems.camera.CameraSystem;
import graphics.Effect;
import model.descriptors.BodyDescriptor;
import model.descriptors.FXDescriptor;
import model.textures.BundledAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class EffectRenderingSystem extends FluidIteratingSystem {

    private CameraSystem cameraSystem;

    private int srcFunc;
    private int dstFunc;
    private SpriteBatch batch;
    private Map<Integer, BundledAnimation> fxs = new HashMap<>();
    private Map<Integer, ParticleEffect> particleEffects = new HashMap<>();

    public EffectRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Effect.class));
        this.batch = batch;
    }

    public SpriteBatch getBatch() {
        return batch;
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
                    ParticleEffect particle = ParticlesHandler.getParticle(effectId);
                    particleEffects.put(entityId, particle);
                    break;
                case FX:
                    FXDescriptor fxDescriptor = DescriptorHandler.getFX(effectId);
                    BundledAnimation bundledAnimation = new BundledAnimation(DescriptorHandler.getGraphic(fxDescriptor.getIndexs()[0]), false);
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
        srcFunc = getBatch().getBlendSrcFunc();
        dstFunc = getBatch().getBlendDstFunc();
        getBatch().enableBlending();
        getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private OrthographicCamera getCamera() {
        return cameraSystem.camera;
    }

    private void doEnd() {
        getBatch().setBlendFunction(srcFunc, dstFunc);
    }

    void drawEffect(E e) {
        if (e.hasEffect()) {
            Effect effect = e.getEffect();
            if (e.hasWorldPos()) {
                drawEffect(e, e.getWorldPos(), Optional.empty());
            } else {
                int networkedEntity = effect.entityReference;
                if (WorldManager.hasNetworkedEntity(networkedEntity)) {
                    int entityId = WorldManager.getNetworkedEntity(networkedEntity);
                    E entity = E(entityId);
                    if (entity != null && entity.hasWorldPos()) {
                        drawEffect(e, entity.getWorldPos(), Optional.of(entityId));
                    }
                }
            }
        }
    }

    private void drawEffect(E e, WorldPos pos, Optional<Integer> ref) {
        doBegin();
        Pos2D screenPos = Util.toScreen(pos.getPos2D());
        Effect effect = e.getEffect();
        int entityId = e.id();
        switch(effect.type) {
            case FX:
                BundledAnimation anim = fxs.get(entityId);
                int effectId = effect.effectId;
                FXDescriptor fxDescriptor = DescriptorHandler.getFX(effectId);
                TextureRegion graphic = anim.getGraphic(false);
                float bodyOffset = ref.map(this::getBodyOffset).orElse(0);
                getBatch().draw(graphic, screenPos.x + (Tile.TILE_PIXEL_WIDTH - graphic.getRegionWidth()) / 2 + fxDescriptor.getOffsetX(), screenPos.y - graphic.getRegionHeight() + fxDescriptor.getOffsetY() + bodyOffset);
                break;
            case PARTICLE:
                ParticleEffect particleEffect = particleEffects.get(entityId);
                particleEffect.draw(getBatch(), world.getDelta());
                break;
        }
        doEnd();
    }

    private int getBodyOffset(int entityId) {
        int headOffsetY = 0;
        if (E(entityId).hasBody()) {
            final Body body = E(entityId).getBody();
            BodyDescriptor bodyDescriptor = DescriptorHandler.getBodies().get(body.index);
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
                        e.deleteFromWorld();
                    } else {
                        anim.setAnimationTime(anim.getAnimationTime() + getWorld().getDelta() * (anim.getFrames().size * 0.33f));
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
