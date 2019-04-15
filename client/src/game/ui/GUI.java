package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import game.managers.AOInputProcessor;
import game.ui.user.UserInformation;


public class GUI {

    private static Inventory inventory;
    private static SpellView spellView;
    private static UserInformation userTable;
    private static DialogText dialog;
    private static AOConsole console;
    private Stage stage;

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
        Pixmap pm = new Pixmap(Gdx.files.internal("data/ui/images/cursor-arrow.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 10, 4));
        pm.dispose();
    }

    private Actor createConsole() {
        Container<Actor> consoleContainer = new Container<>();
        float screenH = Gdx.graphics.getHeight();
        console = new AOConsole();
        console.setY(screenH - 16*AOConsole.MAX_MESSAGES);
        consoleContainer.setActor(console);
        consoleContainer.setPosition(0, screenH - console.getHeight(), Align.top | Align.left);
        return console;
    }

    private Container<Table> createUserStatus() {
        Container<Table> userContainer = new Container<>();
        float width = Gdx.graphics.getWidth() * 30 / 100f;
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
        Container<Table> dialogContainer = new Container<>();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        dialogContainer.setWidth(Inventory.COLUMNS * Slot.SIZE);
        dialogContainer.setHeight(Inventory.COLUMNS * Slot.SIZE);
        dialogContainer.setPosition((screenW - dialogContainer.getWidth()) - 10, (screenH / 2) + 20);
        spellView = new SpellView();
        spellView.setFillParent(true);
        dialogContainer.setActor(spellView);
        return dialogContainer;
    }

    private Container<Table> createInventory() {
        Container<Table> inventoryContainer = new Container<>();
        float width = Gdx.graphics.getWidth() * 20 / 100f;
        inventoryContainer.setWidth(width);
        inventory = new Inventory();
        inventoryContainer.setPosition((Gdx.graphics.getWidth() * 98f / 100f) - width, 1, Align.bottom);
        inventoryContainer.setActor(inventory);
        inventoryContainer.setTransform(true);
        inventoryContainer.setScale(width / inventory.getWidth());
        return inventoryContainer;
    }

    private Container<Table> createDialogContainer() {
        Container<Table> dialogContainer = new Container<>();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float containerW = screenW * 0.8f;
        dialogContainer.setWidth(containerW);
        dialogContainer.setPosition((screenW - containerW) / 2.0f, screenH * 0.25f);
        dialogContainer.fillX();
        dialog = new DialogText();
        dialogContainer.setActor(dialog);
        return dialogContainer;
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
