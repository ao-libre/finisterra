package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.handlers.ParticlesHandler;
import graphics.FX;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Wire(injectInherited = true)
public class ParticleRenderingSystem extends RenderingSystem {

    private Map<Integer, Map<Integer, ParticleEffect>> particles = new HashMap<>();
    private int srcFunc;
    private int dstFunc;

    public ParticleRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(FX.class, WorldPos.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void doBegin() {
        srcFunc = getBatch().getBlendSrcFunc();
        dstFunc = getBatch().getBlendDstFunc();
        getBatch().enableBlending();
        getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
    }

    @Override
    protected void doEnd() {
        getBatch().setBlendFunction(srcFunc, dstFunc);
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);
        particles.remove(entityId);
    }

    @Override
    protected void process(E entity) {
        Pos2D screenPos = Util.toScreen(entity.worldPosPos2D());
        final FX fx = entity.getFX();
        if (fx.particles.isEmpty()) {
            return;
        }
        List<Integer> removeParticles = new ArrayList<>();

        drawParticles(entity.id(), screenPos, fx, removeParticles);

        removeParticles.forEach(fx::removeParticle);
        if (fx.particles.isEmpty()) {
            particles.remove(entity.id());
        }
    }

    private void drawParticles(int entityId, Pos2D screenPos, FX fx, List<Integer> removeParticles) {
        if (fx == null || fx.particles == null) {
            return;
        }
        fx.particles.forEach(effect -> {
            ParticleEffect particleEffect = particles.computeIfAbsent(entityId, id -> new HashMap<>()).computeIfAbsent(effect, eff -> ParticlesHandler.getParticle(eff));
            final float particleX = screenPos.x + (Tile.TILE_PIXEL_WIDTH / 2);
            final float particleY = screenPos.y - 4;
            particleEffect.setPosition(particleX, particleY);
            particleEffect.draw(getBatch(), world.getDelta());
            if (particleEffect.isComplete()) {
                particleEffect.dispose();
                removeParticles.add(effect);
            }
        });
    }
}