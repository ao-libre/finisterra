package game.systems.ui;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import component.camera.Focused;
import component.position.WorldPos;
import game.systems.camera.CameraSystem;
import game.systems.input.InputSystem;
import game.systems.map.TiledMapSystem;
import game.systems.ui.action_bar.ActionBarSystem;
import game.systems.ui.console.ConsoleSystem;
import game.systems.ui.dialog.DialogSystem;
import game.systems.ui.stats.StatsSystem;
import game.systems.ui.user.UserSystem;
import game.utils.Skins;
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
    }

    public void show() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(inputSystem);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);

        actionBarSystem.calculate(entityId);
        userSystem.calculate(entityId);
        statsSystem.calculate(entityId);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        stage.setScrollFocus(table);
        fillTable(table);
    }

    private void fillTable(Table table) {
        final Actor dialogUI = dialogSystem.getActor();
        stage.addActor(dialogUI);

        Container<Actor> consoleUI = new Container<>(consoleSystem.getConsole());
        table.add(consoleUI).top().colspan(2).minHeight(Gdx.graphics.getHeight() * 0.15f).maxHeight(Gdx.graphics.getHeight() * 0.15f);
        table.row();

        Container<Actor> userStatsUI = new Container<>(statsSystem.getActor());
        table.add(userStatsUI).left().expand();
        // Add action bar (inventory and spell view)
        Container<Actor> actionBarUI = new Container<>(actionBarSystem.getActor());
        table.add(actionBarUI).right().expand();
        table.row();
        Container<Actor> userUI = new Container<>(userSystem.getActor());
        table.add(userUI).bottom().colspan(2);

        table.addListener(new InputListener() {

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                return isInUI(dialogUI, x, y) ||
                        isInUI(consoleUI, x, y) ||
                        isInUI(userStatsUI, x, y) ||
                        isInUI(actionBarUI, x, y) ||
                        isInUI(userUI, x, y);
            }

            private boolean isInUI(Actor actor, float x, float y) {
                Vector2 localCoordinates = actor.stageToLocalCoordinates(new Vector2(x, y));
                return isBetween(localCoordinates.x, 0, actor.getWidth())
                        && isBetween(localCoordinates.y, 0, actor.getHeight());
            }

            private boolean isBetween(float x, float x1, float x2) {
                return x >= x1 && x <= x2;
            }
        });
    }

    // Should only process player component.entity
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

    public WorldPos getMouseWorldPos() {
        return getWorldPos(Gdx.input.getX(), Gdx.input.getY());
    }

    public void resize(int width, int height) {
        getStage().getViewport().update(width, height);
    }

    public Stage getStage() {
        return stage;
    }
}
