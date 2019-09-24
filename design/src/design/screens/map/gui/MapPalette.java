package design.screens.map.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static launcher.DesignCenter.SKIN;

public class MapPalette extends Window {

    private final ClickListener mouseListener;
    // palette
    private int layer;
    private Selection selection = Selection.NONE;
    private List<SelectionListener> listeners = new ArrayList<>();

    public MapPalette() {
        super("Palette", SKIN, "main");
        addListener(mouseListener = new ClickListener());
        setMovable(true);

        ButtonGroup layers = new ButtonGroup();
        addLayerButton(0, layers);
        addLayerButton(1, layers);
        addLayerButton(2, layers);
        addLayerButton(3, layers);

        defaults().space(10);

        ButtonGroup buttons = new ButtonGroup();
        buttons.setMinCheckCount(0);
        for (Selection value : Selection.values()) {
            if (value != Selection.NONE) {
                addStateButton(value, buttons, value.name());
            }
        }

    }

    private void addStateButton(Selection state, ButtonGroup buttons, String text) {
        buttons.add(addButton(new TextButton(text, SKIN, "file"), true, (button) -> {
            selection = button.isChecked() ? state : Selection.NONE;
            notifySelection(selection);
        }));
    }

    private void addLayerButton(int i, ButtonGroup layers) {
        layers.add(addButton(new TextButton(i + 1 + "", SKIN, "file"), true, (button) -> {
            layer = i;
            notifySelection(selection);
        }));
    }

    private Button addButton(Button button, boolean grow, Consumer<Button> onClick) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.accept(button);
            }
        });
        add(button).fill(grow, false).expand(grow, false).row();
        return button;
    }

    public void addListener(SelectionListener listener) {
        listeners.add(listener);
    }

    private void notifySelection(Selection selection) {
        listeners.forEach(listener -> listener.selectionChange(selection, layer));
    }

    public int getLayer() {
        return layer;
    }

    public Selection getSelection() {
        return selection;
    }

    public boolean isOver() {
        return mouseListener.isOver() || actualHit() != null;
    }

    private Actor actualHit() {
        int x = Gdx.app.getInput().getX();
        int y = Gdx.app.getInput().getY();
        return hit(x, y, false);
    }

    public enum Selection {
        NONE,
        BLOCK,
        CLEAN,
        TILE_EXIT,
        TILE_SET,
        SELECTION
    }

    public interface SelectionListener {

        void selectionChange(Selection selection, int layer);
    }
}
