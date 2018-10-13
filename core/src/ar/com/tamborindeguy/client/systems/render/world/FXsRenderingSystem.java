package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.handlers.ParticlesHandler;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import graphics.FX;
import position.Pos2D;
import position.WorldPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ar.com.tamborindeguy.client.utils.Constants.MEDITATE_NW_FX;
import static com.artemis.E.E;

@Wire
public class FXsRenderingSystem extends IteratingSystem {

    private SpriteBatch batch;

    private CameraSystem cameraSystem;

    private Map<Integer, Map<Integer, ParticleEffect>> particles = new HashMap<>();

    public FXsRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(FX.class, WorldPos.class));
        this.batch = batch;
    }

    @Override
    protected void process(int entityId) {
        E entity = E(entityId);
        WorldPos worldPos = entity.getWorldPos();
        Pos2D screenPos = Util.toScreen(worldPos);
        final FX fx = entity.getFX();
        List<Integer> toRemove = new ArrayList<>();
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();

        fx.particles.forEach(effect -> {
            ParticleEffect particleEffect = particles.computeIfAbsent(entityId, id -> new HashMap<>()).computeIfAbsent(effect, eff -> ParticlesHandler.getParticle(eff));
            particleEffect.setPosition(screenPos.x - Tile.TILE_PIXEL_WIDTH / 2 - 4, screenPos.y - 2);
            particleEffect.draw(batch, world.getDelta());
            if (particleEffect.isComplete()) {
                toRemove.add(effect);
            }
        });
        batch.end();
        toRemove.forEach(remove -> fx.particles.remove(remove));
        if (fx.particles.isEmpty() && fx.fxs.isEmpty()) {
            entity.removeFX();
            particles.remove(entityId);
        }
    }

}
