package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.AOGame;
import game.managers.AOInputProcessor;
import game.screens.GameScreen;
import game.ui.user.UserInformation;


public class GUI {

    public static final int CONSOLE_TOP_BORDER = 16;
    public static final int CONSOLE_LEFT_BORDER = 5;
    private static ActionBar actionBar;
    private static UserInformation userTable;
    private static DialogText dialog;
    private static AOConsole console;
    private static Stage stage;
    private OrthographicCamera camera;

    public GUI() {
        this.stage = new AOInputProcessor();
    }

    public static ActionBar getActionBar() {
        return actionBar;
    }

    public static Inventory getInventory() {
        return actionBar.getInventory();
    }

    public static DialogText getDialog() {
        return dialog;
    }

    public static AOConsole getConsole() {
        return console;
    }

    public static SpellView getSpellView() {
        return actionBar.getSpellView();
    }

    public static UserInformation getUserTable() {
        return userTable;
    }

    public static Stage getStage() {
        return stage;
    }

    public void initialize() {
        Table table = new Table();
        table.setFillParent(true);
        createConsole(table);
        createUserStatus(table);
        createActionBar(table);
        createDialogContainer(table);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

    }

    public OrthographicCamera getCamera() {
        if (camera == null) {
            if (Gdx.app.getApplicationListener() instanceof AOGame) {
                AOGame game = (AOGame) Gdx.app.getApplicationListener();
                final Screen screen = game.getScreen();
                if (screen instanceof GameScreen) {
                    camera = ((GameScreen) screen).getGUICamera();
                }
            }
        }
        return camera;
    }

    private Actor createConsole(Table table) {
        console = new AOConsole();
        table.add(console).left().top();
        return console;
    }

    private Table createUserStatus(Table table) {
        userTable = new UserInformation();
        table.add(userTable).prefWidth(400).left().bottom().expandX();
        return userTable;
    }

    private Table createActionBar(Table table) {
        actionBar = new ActionBar();
        table.add(actionBar).right().expandY();
        return actionBar;
    }

    private Table createDialogContainer(Table table) {
        dialog = new DialogText();
        float width = getWidth() * 0.8f;
        dialog.setSize(width, dialog.getHeight());
        dialog.setPosition((getWidth() - width) / 2, getHeight() / 2);
        stage.addActor(dialog);
        return dialog;
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

}
