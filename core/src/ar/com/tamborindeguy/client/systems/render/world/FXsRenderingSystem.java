package ar.com.tamborindeguy.client.systems.render.world;

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
import java.util.List;

import static com.artemis.E.E;

@Wire
public class FXsRenderingSystem extends IteratingSystem {

    private SpriteBatch batch;

    private CameraSystem cameraSystem;

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
        List<ParticleEffect> toRemove = new ArrayList<>();
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();

        fx.effects.forEach(effect -> {
            effect.setPosition(screenPos.x - Tile.TILE_PIXEL_WIDTH / 2 - 4, screenPos.y - 2);
            effect.draw(batch, world.getDelta());
            if (effect.isComplete()) {
                toRemove.add(effect);
            }
        });
        batch.end();
        toRemove.forEach(remove -> {
            fx.effects.remove(remove);
        });
        if (fx.effects.isEmpty()) {
            entity.removeFX();
        }
    }

}
