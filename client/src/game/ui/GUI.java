package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.managers.AOInputProcessor;
import game.screens.GameScreen;
import game.ui.user.UserInformation;


public class GUI {

    private static Inventory inventory;
    private static SpellView spellView;
    private static UserInformation userTable;
    private static DialogText dialog;
    private static AOConsole console;
    private Stage stage;
    private OrthographicCamera camera;

    public GUI() {
        this.stage = new AOInputProcessor();
    }

    public void initialize() {
        stage.addActor(createDialogContainer());
        stage.addActor(createInventory());
        stage.addActor(createSpells());
        stage.addActor(createConsole());
        stage.addActor(createUserStatus());
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

    private Actor createConsole() {
        Container<Actor> consoleContainer = new Container<>();
        float screenH = getHeight();
        console = new AOConsole();
        console.setY(screenH - 16 * AOConsole.MAX_MESSAGES);
        consoleContainer.setActor(console);
        consoleContainer.setPosition(0, screenH - console.getHeight(), Align.top | Align.left);
        return console;
    }

    private Container<Table> createUserStatus() {
        Container<Table> userContainer = new Container<>();
        float width = getWidth() * 30 / 100f;
        userContainer.setWidth(width);
        userContainer.setHeight(64);
        float scaleXY = width / (64 + 200);
        userTable = new UserInformation();
        userContainer.setActor(userTable);
        userContainer.setTransform(true);
        userContainer.setScale(scaleXY);
        userContainer.setPosition(1, 1, Align.left | Align.bottom);
        return userContainer;
    }

    private Container<Table> createSpells() {
        Container<Table> spellsContainer = new Container<>();
        float screenW = getWidth();
        float screenH = getHeight();
        float width = screenW * 4f / 100f;
        spellView = new SpellView();
        spellsContainer.setWidth(width);
        spellsContainer.setHeight(SpellView.MAX_SPELLS * SpellSlot.SIZE);
        spellsContainer.setActor(spellView);
        spellsContainer.setTransform(true);
        final float scaleXY = width / SpellSlot.SIZE;
        spellsContainer.setScale(scaleXY);
        spellsContainer.setPosition(getWidth() - 5, (screenH / 2), Align.right);
        return spellsContainer;
    }

    private Container<Table> createInventory() {
        Container<Table> inventoryContainer = new Container<>();
        float width = getWidth() * 20 / 100f;
        inventoryContainer.setWidth(width);
        inventory = new Inventory();
        final float zoom = width / inventory.getWidth();
        inventoryContainer.setScale(zoom);
        inventoryContainer.setPosition((getWidth() * 98f / 100f) - width, 1, Align.right | Align.bottom);
        inventoryContainer.setActor(inventory);
        inventoryContainer.setTransform(true);
        return inventoryContainer;
    }

    private Container<Table> createDialogContainer() {
        Container<Table> dialogContainer = new Container<>();
        float screenW = getWidth();
        float screenH = getHeight();
        float containerW = screenW * 0.8f;
        dialogContainer.setWidth(containerW);
        dialogContainer.setPosition((screenW - containerW) / 2.0f, screenH * 0.25f);
        dialogContainer.fillX();
        dialog = new DialogText();
        dialogContainer.setActor(dialog);
        return dialogContainer;
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    public static Inventory getInventory() {
        return inventory;
    }

    public static DialogText getDialog() {
        return dialog;
    }

    public static AOConsole getConsole() {
        return console;
    }

    public static SpellView getSpellView() {
        return spellView;
    }

    public static UserInformation getUserTable() {
        return userTable;
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

}
