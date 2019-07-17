package design.screens;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.screens.WorldScreen;

public abstract class DesignScreen extends ScreenAdapter implements WorldScreen {

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
    }

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
        Table content = createContent();
        Table buttons = createMenuButtons();
        mainTable.add(buttons).growX().row();
        mainTable.add(content).grow();
        getStage().addActor(getMainTable());
    }

    protected abstract Table createMenuButtons();

    @Override
    public void resize(int width, int height) {
        getStage().getViewport().update(width, height);
    }

    abstract protected Table createContent();

    @Override
    public void dispose() {
        super.dispose();
        getStage().dispose();
    }
}
