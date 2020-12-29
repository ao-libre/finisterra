package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import component.camera.Focused;
import game.systems.render.BatchSystem;
import game.utils.Pos2D;
import game.utils.Resources;
import shared.model.map.Tile;

import static game.utils.Resources.GAME_SHADERS_LIGHT;

@Wire(injectInherited = true)
public class LightRenderingSystem extends RenderingSystem {

    private final Texture light;
    private final float width;
    private final float height;
    private FrameBuffer lightBuffer;
    private Color prevColor;
    private int blendDstFunc;
    private int blendSrcFunc;

    private BatchSystem batchSystem;

    public LightRenderingSystem() {
        super(Aspect.all(Focused.class));
        light = new Texture(Gdx.files.internal(Resources.GAME_SHADERS_PATH + GAME_SHADERS_LIGHT));

        width = Tile.TILE_PIXEL_WIDTH * 32f;
        height = Tile.TILE_PIXEL_WIDTH * 32f;
    }

    @Override
    protected void initialize() {
        super.initialize();
        resize(width, height);
    }

    @Override
    protected void doBegin() {
        final SpriteBatch batch = batchSystem.getBatch();

        prevColor = batch.getColor();
        blendDstFunc = batch.getBlendDstFunc();
        blendSrcFunc = batch.getBlendSrcFunc();

    }

    @Override
    protected void doEnd() {
        final SpriteBatch batch = batchSystem.getBatch();
        batch.setColor(prevColor);
        batch.setBlendFunction(blendSrcFunc, blendDstFunc);
    }

    @Override
    protected void process(E playerEntity) {
        Pos2D pos = Pos2D.get(playerEntity);
        renderLight(pos);
    }

    private void renderLight(Pos2D pos) {
        Pos2D playerPosition = pos.toScreen();

        float tx = playerPosition.x + Tile.TILE_PIXEL_WIDTH / 2;
        float ty = playerPosition.y;
        float x = tx - width / 2;
        float y = ty - height / 2;
        final SpriteBatch batch = batchSystem.getBatch();
        batch.draw(lightBuffer.getColorBufferTexture(), x, y, width, height);
    }

    public void resize(float width, float height) {
        // Faked light system (alpha blending)
        // if lightBuffer was created before, dispose, we recreate a new one
        if (lightBuffer != null)
            lightBuffer.dispose();
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) width, (int) height, false);
        lightBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        prepareBuffer();
    }

    private void prepareBuffer() {
        lightBuffer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        final SpriteBatch batch = batchSystem.getBatch();
        Color color = batch.getColor();

        batch.setColor(0.8f, 0.8f, 0.8f, 1f);
        int lightWidth = (int) Tile.TILE_PIXEL_WIDTH * 30;
        int lightHeight = (int) Tile.TILE_PIXEL_HEIGHT * 28;
        batch.begin();
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(light, 0, 0, lightWidth, lightHeight);
        batch.end();
        batch.setColor(color);
        lightBuffer.end();
    }
}
