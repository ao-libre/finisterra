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
public class ParticleRenderingSystem extends IteratingSystem {

    private SpriteBatch batch;

    private CameraSystem cameraSystem;

    private Map<Integer, Map<Integer, ParticleEffect>> particles = new HashMap<>();
    private int srcFunc;
    private int dstFunc;

    public ParticleRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(FX.class, WorldPos.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        // remember SpriteBatch's current functions
        srcFunc = batch.getBlendSrcFunc();
        dstFunc = batch.getBlendDstFunc();
        batch.enableBlending();
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        particles.remove(entityId);
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        Pos2D screenPos = Util.toScreen(entity.worldPosPos2D());
        final FX fx = entity.getFX();
        if (fx.particles.isEmpty()) {
            return;
        }
        List<Integer> removeParticles = new ArrayList<>();

        drawParticles(entityId, screenPos, fx, removeParticles);

        removeParticles.forEach(remove -> fx.removeParticle(remove));
        if (fx.particles.isEmpty()) {
            particles.remove(entityId);
        }
    }

    private void drawParticles(int entityId, Pos2D screenPos, FX fx, List<Integer> removeParticles) {
        if (fx == null || fx.particles == null) {
            return;
        }
        fx.particles.forEach(effect -> {
            ParticleEffect particleEffect = particles.computeIfAbsent(entityId, id -> new HashMap<>()).computeIfAbsent(effect, eff -> ParticlesHandler.getParticle(eff));
            final float particleX = screenPos.x - (Tile.TILE_PIXEL_WIDTH / 2);
            final float particleY = screenPos.y - 4;
            particleEffect.setPosition(particleX, particleY);
            particleEffect.draw(batch, world.getDelta());
            if (particleEffect.isComplete()) {
                particleEffect.dispose();
                removeParticles.add(effect);
            }
        });
    }

    @Override
    protected void end() {
        batch.setBlendFunction(srcFunc, dstFunc);
        batch.end();
    }
}
