package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.character.states.Immobile;
import game.screens.GameScreen;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.utils.Fonts;
import game.utils.Resources;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;

import static com.artemis.E.E;
import static game.utils.Resources.GAME_SHADERS_LIGHT;

@Wire
public class LightRenderingSystem extends OrderedEntityProcessingSystem {

    private final SpriteBatch batch;
    private final Texture light;
    private final int tw;
    private final int th;
    private CameraSystem cameraSystem;

    public LightRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class));
        this.batch = batch;
        light = new Texture(Gdx.files.internal(Resources.GAME_SHADERS_PATH + GAME_SHADERS_LIGHT));
        tw = light.getWidth();
        th = light.getHeight();
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);


    }

    @Override
    protected void end() {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        final Color color = new Color(batch.getColor());
        batch.getColor().a = 0.5f;
//        batch.draw(light, cameraSystem.camera.position.x - (tw / 2), cameraSystem.camera.position.y - (th / 2));
        batch.setColor(color);
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
