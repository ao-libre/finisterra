package game.systems.ui;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import component.camera.Focused;
import component.position.WorldPos;
import game.systems.camera.CameraSystem;
import game.systems.input.InputSystem;
import game.systems.map.TiledMapSystem;
import game.systems.ui.action_bar.ActionBarSystem;
import game.systems.ui.console.ConsoleSystem;
import game.systems.ui.dialog.DialogSystem;
import game.systems.ui.stats.StatsSystem;
import game.systems.ui.stats.UserBars;
import game.systems.ui.stats.UserHeader;
import game.systems.ui.stats.UserStats;
import game.systems.ui.user.UserSystem;
import game.ui.WidgetFactory;
import game.utils.Skins;
import shared.util.WorldPosConversion;

import static com.artemis.E.E;

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
        ObjectMap<String, BitmapFont> fonts = Skins.CURRENT.get().getAll(BitmapFont.class);
        if (fonts != null) {
            fonts.forEach((f) -> f.value.setUseIntegerPositions(false));
        }
    }

    public void show() {
        // Configura tooltips
        TooltipManager.getInstance().initialTime = 0.25f;
        TooltipManager.getInstance().subsequentTime = 0.25f;

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
        fillTable(table, E(entityId));
    }

    private void fillTable(Table table, E e) {
        final Actor dialogUI = dialogSystem.getActor();
        stage.addActor(dialogUI);
        Table leftTable = new Table();
        leftTable.add(consoleSystem.getConsole()).top().left().padLeft(20).padTop(10).minHeight(Gdx.graphics.getHeight() * 0.15f).maxHeight(Gdx.graphics.getHeight() * 0.15f).row();

        Container<Actor> userStatsUI = new Container<>(statsSystem.getActor());
        leftTable.add(userStatsUI).left().grow();
        table.add(leftTable).left().grow();

        // Add action bar (inventory and spell view)
        Container<Actor> actionBarUI = new Container<>();
        Table rightTable = WidgetFactory.createMainTable();
        Table optionButtons1 = new Table();
        optionButtons1.add(WidgetFactory.createImageButton(WidgetFactory.ImageButtons.BACK)).size(48).grow();
        optionButtons1.add(WidgetFactory.createImageButton(WidgetFactory.ImageButtons.CLOSE)).size(48).grow();
        optionButtons1.add(WidgetFactory.createImageButton(WidgetFactory.ImageButtons.DELETE)).size(48).grow();
        rightTable.add(optionButtons1).top().height(48).growX().row();

        rightTable.add(new UserHeader(e)).pad(15).growX().row();

        rightTable.add(actionBarSystem.getActor()).height(410).padRight(-1).padTop(-55).row();
        rightTable.add(new UserBars(e)).top().padTop(-50).grow().row();
        rightTable.add(new UserStats(e)).grow().row();

        Table optionButtons2 = new Table();
        optionButtons2.add(WidgetFactory.createImageButton(WidgetFactory.ImageButtons.BACK)).size(48).grow();
        optionButtons2.add(WidgetFactory.createImageButton(WidgetFactory.ImageButtons.CLOSE)).size(48).grow();
        optionButtons2.add(WidgetFactory.createImageButton(WidgetFactory.ImageButtons.DELETE)).size(48).grow();
        rightTable.add(optionButtons2).bottom().height(45).growX();
        table.add(rightTable).right().bottom().growY();

        table.addListener(new InputListener() {

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                return isInUI(dialogUI, x, y) ||
                        isInUI(consoleSystem.getConsole(), x, y) ||
                        isInUI(userStatsUI, x, y) ||
                        isInUI(actionBarUI, x, y) ||
                        isInUI(userSystem.getActor(), x, y);
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

//        table.add(userSystem.getActor()).bottom().colspan(2).expand();
    }

    // Should only process player component.entity
    @Override
    protected void process(int entityId) {
//        stage.act(Gdx.graphics.getDeltaTime());
//        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public WorldPos getWorldPos(int screenX, int screenY) { // TODO review
        Vector3 screenPos = cameraSystem.camera.unproject(new Vector3(screenX, screenY, 0), 0, 0,
                Gdx.graphics.getWidth() - 260, Gdx.graphics.getHeight());
        WorldPos worldPos = WorldPosConversion.toWorld(screenPos.x, screenPos.y);
        worldPos.map = mapSystem.mapNumber;
        return worldPos;
    }

    public WorldPos getMouseWorldPos() {
        return getWorldPos(Gdx.input.getX(), Gdx.input.getY());
    }

    public void resize(int width, int height) {
//        getStage().getViewport().update(width, height);
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isWideScreen() {
        float normalRatio = 800.f / 600.f;
        float height = Gdx.graphics.getHeight();
        float width = Gdx.graphics.getWidth();
        float result = width / height;
        return result != normalRatio;
    }
}
