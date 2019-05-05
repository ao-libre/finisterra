package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import game.screens.GameScreen;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.utils.Resources;
import position.Pos2D;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;

import static com.artemis.E.E;
import static game.utils.Resources.GAME_SHADERS_LIGHT;

@Wire
public class LightRenderingSystem extends OrderedEntityProcessingSystem {

    private final SpriteBatch batch;
    private final Texture light;
    private final float width;
    private final float height;
    FrameBuffer lightBuffer;
    TextureRegion lightBufferRegion;
    private CameraSystem cameraSystem;
    private Color prevColor;
    private int blendDstFunc;
    private int blendSrcFunc;

    public LightRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class));
        this.batch = batch;
        light = new Texture(Gdx.files.internal(Resources.GAME_SHADERS_PATH + GAME_SHADERS_LIGHT));

        width = Tile.TILE_PIXEL_WIDTH * 32f;
        height = Tile.TILE_PIXEL_WIDTH * 32f;
        resize(width, height);
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        prevColor = batch.getColor();
        blendDstFunc = batch.getBlendDstFunc();
        blendSrcFunc = batch.getBlendSrcFunc();
    }

    @Override
    protected void end() {
        batch.setColor(prevColor);
        batch.setBlendFunction(blendSrcFunc, blendDstFunc);
    }

    @Override
    protected void process(Entity e) {
        int player = GameScreen.getPlayer();
        E playerEntity = E(player);
        if (player < 0 || playerEntity == null) {
            return;
        }
        Pos2D pos = playerEntity.worldPosPos2D();
        renderLight(pos);
    }

    private void renderLight(Pos2D pos) {
        Pos2D playerPosition = Util.toScreen(pos);

        lightBuffer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(0.8f, 0.8f, 0.8f, 1f);
        float tx = playerPosition.x;
        float ty = playerPosition.y;
        int lightWidth = (int) (light.getWidth() * 2.5f);
        int lightHeight = (int) (light.getHeight() * 1.5f);
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(light, tx - lightWidth / 2, ty - lightHeight / 2, lightWidth, lightHeight);
        batch.end();
        lightBuffer.end();


        batch.begin();
        batch.draw(lightBuffer.getColorBufferTexture(), tx - width / 2, ty - height / 2, width, height);
        batch.end();
    }

    public void resize(float width, float height) {
        // Fakedlight system (alpha blending)
        // if lightBuffer was created before, dispose, we recreate a new one
        if (lightBuffer != null)
            lightBuffer.dispose();
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) width, (int) height, false);

        lightBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
