package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.managers.AOInputProcessor;
import game.utils.Skins;

public abstract class AbstractScreen extends ScreenAdapter {

    private static final Skin SKIN = Skins.COMODORE_SKIN;
    private final Stage stage;
    private Table mainTable;

    public AbstractScreen() {
        stage = new Stage();
        createUI();
    }

    public Stage getStage() {
        return stage;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public Skin getSkin() {
        return SKIN;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void render(float delta) {
        getStage().act(delta);
        getStage().draw();
    }

    void createUI() {
        mainTable = new Table(Skins.COMODORE_SKIN);
        mainTable.setFillParent(true);
        createContent();
        getStage().addActor(getMainTable());
    }

    @Override public void resize(int width, int height) {
        getStage().getViewport().update(width, height, true);
    }

    abstract void createContent();

    @Override public void dispose() {
        super.dispose();
        getStage().dispose();
    }
}
