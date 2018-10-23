package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.managers.AOInputProcessor;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.utils.WorldUtils;
import ar.com.tamborindeguy.network.combat.SpellCastRequest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Optional;

public class GUI {

    private static Inventory inventory;
    private static SpellView spellView;
    private static DialogText dialog;
    private Stage stage;

    public GUI() {
        this.stage = new AOInputProcessor();
    }

    public void initialize() {
        stage.addActor(createDialogContainer());
        stage.addActor(createInventory());
        stage.addActor(createSpells());
        Gdx.input.setInputProcessor(stage);
    }

    private Container<Table> createSpells() {
        Container<Table> dialogContainer = new Container<>();
        float screenW = Gdx.graphics.getWidth();
        dialogContainer.setWidth(Inventory.COLUMNS * Slot.SIZE * Inventory.ZOOM);
        dialogContainer.setHeight(Inventory.COLUMNS * Slot.SIZE * Inventory.ZOOM);
        dialogContainer.setPosition((screenW - dialogContainer.getWidth()) - 10, 0);
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
        dialogContainer.setPosition((screenW - dialogContainer.getWidth()) - 10, (screenH - dialogContainer.getHeight() - 20) / 2);
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
