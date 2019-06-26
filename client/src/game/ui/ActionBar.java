package game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.ui.SwitchButtons.ActionSwitchListener;
import game.ui.SwitchButtons.State;
import game.utils.Skins;

public class ActionBar extends Table implements ActionSwitchListener {

    private static final float PAD_TOP = -17f;
    private SwitchButtons buttons;
    private SpellView spellView;
    private Inventory inventory;

    ActionBar() {
        super(Skins.COMODORE_SKIN);
        buttons = new SwitchButtons();
        buttons.addListener(this);
        spellView = new SpellView();
        inventory = new Inventory();

        add(buttons).top().row();
        add(inventory).padTop(PAD_TOP);
    }

    @Override
    public void notify(State state) {
        switch (state) {
            case SPELLS:
                clear();
                add(buttons).top().row();
                add(spellView).padTop(PAD_TOP);
                break;
            case INVENTORY:
                clear();
                add(buttons).top().row();
                add(inventory).padTop(PAD_TOP);
                break;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public SpellView getSpellView() {
        return spellView;
    }
}
