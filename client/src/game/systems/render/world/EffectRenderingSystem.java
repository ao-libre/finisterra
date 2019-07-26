package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.character.parts.Body;
import game.handlers.AOAssetManager;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.handlers.ParticlesHandler;
import game.managers.WorldManager;
import game.systems.camera.CameraSystem;
import graphics.Effect;
import model.descriptors.BodyDescriptor;
import model.descriptors.FXDescriptor;
import model.textures.AOAnimation;
import model.textures.BundledAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.model.Graphic;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class EffectRenderingSystem extends FluidIteratingSystem {

    private CameraSystem cameraSystem;
    private WorldManager worldManager;
    private DescriptorHandler descriptorHandler;
    private AnimationHandler animationHandler;

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
                    FXDescriptor fxDescriptor = descriptorHandler.getFX(effectId);
                    BundledAnimation bundledAnimation = animationHandler.getAnimation(fxDescriptor.getIndexs()[0]);
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
        getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
    }

    private void doEnd() {
        getBatch().setBlendFunction(srcFunc, dstFunc);
    }

    void drawEffect(E e, Optional<WorldPos> forcePos) {
        if (e.hasEffect()) {
            Effect effect = e.getEffect();
            if (forcePos.isPresent()) {
                drawEffect(e, forcePos.get());
            } else {
                int networkedEntity = effect.entityReference;
                if (worldManager.hasNetworkedEntity(networkedEntity)) {
                    int entityId = worldManager.getNetworkedEntity(networkedEntity);
                    E entity = E(entityId);
                    if (entity != null && entity.hasWorldPos()) {
                        drawEffect(e, entity.getWorldPos());
                    }
                } else if (e.hasWorldPos()) {
                    drawEffect(e, e.getWorldPos());
                }
            }
        }
    }

    private void drawEffect(E e, WorldPos pos) {
        doBegin();
        Pos2D screenPos = Util.toScreen(pos.getPos2D());
        Effect effect = e.getEffect();
        int entityId = e.id();
        switch (effect.type) {
            case FX:
                BundledAnimation anim = fxs.get(entityId);
                int effectId = effect.effectId;
                FXDescriptor fxDescriptor = descriptorHandler.getFX(effectId);
                TextureRegion graphic = anim.getGraphic();
                getBatch().draw(graphic, screenPos.x + (Tile.TILE_PIXEL_WIDTH - graphic.getRegionWidth()) / 2 + fxDescriptor.getOffsetX(), screenPos.y - graphic.getRegionHeight() + 20 + fxDescriptor.getOffsetY());
                break;
            case PARTICLE:
                ParticleEffect particleEffect = particleEffects.get(entityId);
                float x = particleEffect.getBoundingBox().getWidth();
                particleEffect.setPosition(screenPos.x + Tile.TILE_PIXEL_WIDTH / 2, screenPos.y);
                particleEffect.draw(getBatch(), world.getDelta());
                break;
        }
        doEnd();
    }

    private int getBodyOffset(int entityId) {
        int headOffsetY = 0;
        if (E(entityId).hasBody()) {
            final Body body = E(entityId).getBody();
            BodyDescriptor bodyDescriptor = descriptorHandler.getBody(body.index);
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
                        worldManager.getNetworkedId(id).ifPresent(worldManager::unregisterEntity);
                    } else {
                        anim.setAnimationTime(anim.getAnimationTime() + getWorld().getDelta() * (anim.getAnimation().getKeyFrames().length* 0.33f));
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
