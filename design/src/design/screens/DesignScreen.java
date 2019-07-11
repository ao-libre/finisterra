package design.screens;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import game.utils.Resources;
import game.utils.Skins;

public abstract class DesignScreen extends ScreenAdapter {

    private final Stage stage;
    private Table mainTable;
    World world;

    public DesignScreen() {
        stage = new Stage() {
            @Override
            public boolean keyUp(int keyCode) {
                keyPressed(keyCode);
                return super.keyUp(keyCode);
            }
        };
        world = createWorld();
    }

    public World getWorld() {
        return world;
    }

    protected abstract World createWorld();

    protected abstract void keyPressed(int keyCode);

    public Stage getStage() {
        return stage;
    }

    public Table getMainTable() {
        return mainTable;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getStage().act(delta);
        getStage().draw();
        if (world != null) {
            world.setDelta(delta);
            world.process();
        }
    }

    protected void createUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        createContent();
        getStage().addActor(getMainTable());
    }

    @Override
    public void resize(int width, int height) {
        getStage().getViewport().update(width, height);
    }

    abstract protected void createContent();

    @Override
    public void dispose() {
        super.dispose();
        getStage().dispose();
    }
}
