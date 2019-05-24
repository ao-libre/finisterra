package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import game.utils.Resources;
import position.Pos2D;
import shared.model.map.Tile;
import shared.util.Util;

import static game.utils.Resources.GAME_SHADERS_LIGHT;

@Wire(injectInherited=true)
public class LightRenderingSystem extends RenderingSystem {

    private final Texture light;
    private final float width;
    private final float height;
    FrameBuffer lightBuffer;
    TextureRegion lightBufferRegion;
    private Color prevColor;
    private int blendDstFunc;
    private int blendSrcFunc;

    public LightRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class), batch, CameraKind.WORLD);
        light = new Texture(Gdx.files.internal(Resources.GAME_SHADERS_PATH + GAME_SHADERS_LIGHT));

        width = Tile.TILE_PIXEL_WIDTH * 32f;
        height = Tile.TILE_PIXEL_WIDTH * 32f;
        resize(width, height);
    }

    @Override
    protected void begin() {
        getCamera().update();
        getBatch().setProjectionMatrix(getCamera().combined);
        doBegin();
    }

    @Override
    protected void end() {
        doEnd();
    }

    @Override
    protected void doBegin() {
        prevColor = getBatch().getColor();
        blendDstFunc = getBatch().getBlendDstFunc();
        blendSrcFunc = getBatch().getBlendSrcFunc();
    }

    @Override
    protected void doEnd() {
        getBatch().setColor(prevColor);
        getBatch().setBlendFunction(blendSrcFunc, blendDstFunc);
    }

    @Override
    protected void process(E playerEntity) {
        Pos2D pos = playerEntity.worldPosPos2D();
        renderLight(pos);
    }

    private void renderLight(Pos2D pos) {
        Pos2D playerPosition = Util.toScreen(pos);

        lightBuffer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getBatch().begin();
        getBatch().setColor(0.8f, 0.8f, 0.8f, 1f);
        float tx = playerPosition.x;
        float ty = playerPosition.y;
        int lightWidth = (int) Tile.TILE_PIXEL_WIDTH * 8;
        int lightHeight = (int) Tile.TILE_PIXEL_HEIGHT * 7;
        getBatch().enableBlending();
        getBatch().setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        getBatch().draw(light, tx - (lightWidth >> 1), ty - (lightHeight >> 1), lightWidth, lightHeight);
        getBatch().end();
        lightBuffer.end();

        getBatch().begin();
        getBatch().draw(lightBuffer.getColorBufferTexture(), tx - width / 2, ty - height / 2, width, height);
        getBatch().end();
    }

    public void resize(float width, float height) {
        // Fakedlight system (alpha blending)
        // if lightBuffer was created before, dispose, we recreate a new one
        if (lightBuffer != null)
            lightBuffer.dispose();
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) width, (int) height, false);

        lightBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

}
