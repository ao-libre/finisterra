package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.handlers.DescriptorHandler;
import ar.com.tamborindeguy.client.handlers.ParticlesHandler;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.descriptors.BodyDescriptor;
import ar.com.tamborindeguy.model.descriptors.FXDescriptor;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.model.textures.BundledAnimation;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.minlog.Log;
import entity.Body;
import graphics.FX;
import position.Pos2D;
import position.WorldPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.artemis.E.E;

@Wire
public class FXsRenderingSystem extends IteratingSystem {

    private SpriteBatch batch;

    private CameraSystem cameraSystem;

    private Map<Integer, Map<Integer, ParticleEffect>> particles = new HashMap<>();
    private Map<Integer, Map<Integer, BundledAnimation>> fxs = new HashMap<>();

    public FXsRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(FX.class, WorldPos.class));
        this.batch = batch;
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        particles.remove(entityId);
        fxs.remove(entityId);
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        Pos2D screenPos = Util.toScreen(entity.getPos2D());
        final FX fx = entity.getFX();
        List<Integer> removeParticles = new ArrayList<>();
        List<Integer> removeFXs = new ArrayList<>();
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        // remember SpriteBatch's current functions
        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        batch.enableBlending();
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
        drawFXs(entityId, screenPos, fx, removeFXs);
        drawParticles(entityId, screenPos, fx, removeParticles);
        batch.end();
        batch.setBlendFunction(srcFunc, dstFunc);

        removeParticles.forEach(remove -> fx.removeParticle(remove));
        removeFXs.forEach(remove -> fx.removeFx(remove));
        if (fx.particles.isEmpty()) {
            particles.remove(entityId);
        }
        if (fx.fxs.isEmpty()) {
            fxs.remove(entityId);
        }
        if (fx.particles.isEmpty() && fx.fxs.isEmpty()) {
            entity.removeFX();
        }
    }

    private void drawParticles(int entityId, Pos2D screenPos, FX fx, List<Integer> removeParticles) {
        fx.particles.forEach(effect -> {
            ParticleEffect particleEffect = particles.computeIfAbsent(entityId, id -> new HashMap<>()).computeIfAbsent(effect, eff -> ParticlesHandler.getParticle(eff));
            particleEffect.setPosition(screenPos.x - Tile.TILE_PIXEL_WIDTH / 2, screenPos.y - 2);
            particleEffect.draw(batch, world.getDelta());
            if (particleEffect.isComplete()) {
                particleEffect.dispose();
                removeParticles.add(effect);
            }
        });
    }

    private void drawFXs(int entityId, Pos2D screenPos, FX fx, List<Integer> removeFXs) {
        fx.fxs.forEach(fxId -> {
            FXDescriptor fxDescriptor = DescriptorHandler.getFX(fxId);
            Map<Integer, BundledAnimation> anims = fxs.computeIfAbsent(entityId, id -> new HashMap<>());
            int bodyOffset = getBodyOffset(entityId);
            BundledAnimation anim = anims.computeIfAbsent(fxId, fxGraphic -> new BundledAnimation(DescriptorHandler.getGraphic(fxDescriptor.getIndexs()[0])));
            TextureRegion graphic = anim.getGraphic(false);
            batch.draw(graphic, screenPos.x - (Tile.TILE_PIXEL_WIDTH + graphic.getRegionWidth()) / 2 + fxDescriptor.getOffsetX(), screenPos.y - graphic.getRegionHeight() + fxDescriptor.getOffsetY() + bodyOffset);
            anim.setAnimationTime(anim.getAnimationTime() + getWorld().getDelta() * (anim.getFrames().size * 0.33f));
            if (anim.getAnimation().isAnimationFinished(anim.getAnimationTime())) {
                removeFXs.add(fxId);
                anims.remove(fxId);
            }
        });
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

}
