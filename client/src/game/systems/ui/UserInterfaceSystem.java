package game.systems.ui;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import game.systems.camera.CameraSystem;
import game.systems.input.InputSystem;
import game.systems.map.TiledMapSystem;
import game.systems.ui.action_bar.ActionBarSystem;
import game.systems.ui.console.ConsoleSystem;
import game.systems.ui.dialog.DialogSystem;
import game.systems.ui.stats.StatsSystem;
import game.systems.ui.user.UserSystem;
import game.utils.Skins;
import position.WorldPos;
import shared.util.WorldPosConversion;

@Wire
public class UserInterfaceSystem extends IteratingSystem implements Disposable {

    private CameraSystem cameraSystem;
    private TiledMapSystem mapSystem;
    private InputSystem inputSystem;

    private ConsoleSystem consoleSystem;
    private ActionBarSystem actionBarSystem;
    private UserSystem userSystem;
    private StatsSystem statsSystem;
    private DialogSystem dialogSystem;

    private Stage stage;

    public UserInterfaceSystem() {
        super(Aspect.all(Focused.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        stage = new Stage();

        Skins.COMODORE_SKIN.getFont("simple").setUseIntegerPositions(false);
        Skins.COMODORE_SKIN.getFont("simple-with-border").setUseIntegerPositions(false);
        Skins.COMODORE_SKIN.getFont("flipped").setUseIntegerPositions(false);
        Skins.COMODORE_SKIN.getFont("flipped-with-border").setUseIntegerPositions(false);

        Table table = new Table();
        table.setFillParent(true);
        fillTable(table);

        stage.addActor(table);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(inputSystem);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void fillTable(Table table) {
        stage.addActor(dialogSystem.getActor());

        table.setDebug(true);
        table.add(consoleSystem.getActor()).top().colspan(2).minHeight(Gdx.graphics.getHeight() * 0.15f).maxHeight(Gdx.graphics.getHeight() * 0.15f);
        table.row();

        table.add(new Container<>(statsSystem.getActor())).left().expand();
        table.add(new Container<>(actionBarSystem.getActor())).right().expand();
        table.row();
        table.add(new Container<>(userSystem.getActor())).bottom().colspan(2);
    }

    // Should only process player entity
    @Override
    protected void process(int entityId) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public WorldPos getWorldPos(int screenX, int screenY) { // TODO review
        Vector3 screenPos = cameraSystem.camera.unproject(new Vector3(screenX, screenY, 0));
        WorldPos worldPos = WorldPosConversion.toWorld(screenPos.x, screenPos.y);
        worldPos.map = mapSystem.mapNumber;
        return worldPos;
    }

    public void resize(int width, int height) {
        getStage().getViewport().update(width, height);
    }

    public Stage getStage() {
        return stage;
    }
}