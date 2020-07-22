package game.systems.render;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.esotericsoftware.minlog.Log;
import component.camera.Focused;
import component.position.WorldPos;
import game.utils.Pos2D;
import game.utils.Resources;
import shared.model.map.Tile;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import static game.utils.Resources.GAME_SHADERS_LIGHT;

@Wire
public class BatchRenderingSystem extends BaseSystem {

    private final Texture light;
    private final float width;
    private final float height;
    private final Batch batch;
    private final Deque<BatchTask> tasks = new ConcurrentLinkedDeque<>();
    private FrameBuffer lightBuffer;

    public BatchRenderingSystem() {
        this.batch = new SpriteBatch();
        this.light = new Texture(Gdx.files.internal(Resources.GAME_SHADERS_PATH + GAME_SHADERS_LIGHT));
        width = Tile.TILE_PIXEL_WIDTH * 32f;
        height = Tile.TILE_PIXEL_WIDTH * 32f;
        resize(width, height);
    }

    public void resize(float width, float height) {
        // Faked light system (alpha blending)
        // if lightBuffer was created before, dispose, we recreate a new one
        if (lightBuffer != null) {
            lightBuffer.dispose();
        }
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) width, (int) height, false);
        lightBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    protected void processSystem() {
        drawAll();
        Iterator<E> iterator = E.withAspect(Aspect.all(Focused.class, WorldPos.class)).iterator();
        if (iterator.hasNext()) {
            E player = iterator.next();
            Pos2D playerPosition = Pos2D.get(player).toScreen();
            renderLight(playerPosition);
        }
    }

    private void renderLight(Pos2D playerPosition) {
        float tx = playerPosition.x + Tile.TILE_PIXEL_WIDTH / 2;
        float ty = playerPosition.y;
        int lightWidth = (int) Tile.TILE_PIXEL_WIDTH * 30;
        int lightHeight = (int) Tile.TILE_PIXEL_HEIGHT * 28;

        lightBuffer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getBatch().begin();
        getBatch().setColor(0.8f, 0.8f, 0.8f, 1f);
        getBatch().enableBlending();
        getBatch().setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        getBatch().draw(light, tx - (lightWidth >> 1), ty - (lightHeight >> 1), lightWidth, lightHeight);
        getBatch().end();
        lightBuffer.end();

        getBatch().begin();
        getBatch().draw(lightBuffer.getColorBufferTexture(), tx - width / 2, ty - height / 2, width, height);
        getBatch().end();
    }

    private void drawAll() {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        while (tasks.peek() != null) {
            tasks.poll().run(batch);
        }
        batch.end();
    }

    public void addTask(BatchTask task) {
        tasks.offer(task);
    }

    public Batch getBatch() {
        return batch;
    }
}

