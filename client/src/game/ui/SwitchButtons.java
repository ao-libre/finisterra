package game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public class SwitchButtons extends Table {

    private final TextButton inventory;
    private final TextButton spells;
    private final List<ActionSwitchListener> listeners = new ArrayList<>();
    private State state = State.INVENTORY;

    public SwitchButtons() {
        inventory = WidgetFactory.createImageInventoryButton();
        spells = WidgetFactory.createImageSpellsButton();
        inventory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle(State.INVENTORY);
            }
        });
        spells.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle(State.SPELLS);
            }
        });
        add(inventory).grow().left();
        add(spells).grow().right();
        toggle(State.INVENTORY);
    }

    public void addListener(ActionSwitchListener listener) {
        listeners.add(listener);
    }

    private void toggle(State state) {
        boolean notify = true;
        if (this.state == state) {
            notify = false;
        }
        this.state = state;
        switch (state) {
            case SPELLS -> {
                inventory.setChecked(false);
                spells.setChecked(true);
            }
            case INVENTORY -> {
                spells.setChecked(false);
                inventory.setChecked(true);
            }
        }
        if (notify) listeners.forEach(listener -> listener.notify(this.state));
    }

    public void toggle() {
        switch (state) {
            case INVENTORY -> toggle(State.SPELLS);
            case SPELLS -> toggle(State.INVENTORY);
        }

    }

    public enum State {
        INVENTORY,
        SPELLS
    }

    public interface ActionSwitchListener {
        void notify(State state);
    }
}
