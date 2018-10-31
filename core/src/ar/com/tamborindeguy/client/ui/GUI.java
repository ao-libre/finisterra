package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.managers.AOInputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;


public class GUI {

    private static Inventory inventory;
    private static SpellView spellView;
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
        Gdx.input.setInputProcessor(stage);
        Pixmap pm = new Pixmap(Gdx.files.internal("data/ui/images/cursor-arrow.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 10, 4));
        pm.dispose();
    }

    private Actor createConsole() {
//        Container<Actor> consoleContainer = new Container<>();
//        float screenW = Gdx.graphics.getWidth();
//        float screenH = Gdx.graphics.getHeight();
//        consoleContainer.setWidth(screenW / 2);
//        consoleContainer.setHeight(screenH / 10);
        console = new AOConsole();
        console.setPosition(0, 0);
//        consoleContainer.setActor(console);
        return console;
    }

    private Container<Table> createSpells() {
        Container<Table> dialogContainer = new Container<>();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        dialogContainer.setWidth(Inventory.COLUMNS * Slot.SIZE * Inventory.ZOOM);
        dialogContainer.setHeight(Inventory.COLUMNS * Slot.SIZE * Inventory.ZOOM);
        dialogContainer.setPosition((screenW - dialogContainer.getWidth()) - 10, (screenH / 2) + 20);
        spellView = new SpellView();
        spellView.setFillParent(true);
        dialogContainer.setActor(spellView);
        return dialogContainer;
    }

    private Container<Table> createInventory() {
        Container<Table> dialogContainer = new Container<>();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float containerW = Inventory.COLUMNS * Slot.SIZE * Inventory.ZOOM;
        dialogContainer.setWidth(containerW);
        dialogContainer.setHeight(Inventory.ROWS * Slot.SIZE * Inventory.ZOOM);
        dialogContainer.setPosition((screenW - dialogContainer.getWidth()) - 10, (screenH / 2) - dialogContainer.getHeight());
        inventory = new Inventory();
        dialogContainer.setActor(inventory);
        return dialogContainer;
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

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

}
