package game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.ui.SwitchButtons.ActionSwitchListener;
import game.ui.SwitchButtons.State;
import game.utils.Skins;


public final class ActionBar extends Table implements ActionSwitchListener {

    private static final float PAD_TOP = -17f;
    private final ClickListener mouseListener;
    private final SwitchButtons buttons;
    private final SpellView spellView;
    private final SpellViewExpanded spellViewExpanded;
    private final Inventory inventory;
    private final InventoryQuickBar inventoryQuickBar;
    private ImageTextButton expandButton;
    private String currentState = "INVENTORY";

    ActionBar() {
        super(Skins.COMODORE_SKIN);
        this.mouseListener = new ClickListener();
        this.buttons = new SwitchButtons();
        this.buttons.addListener(this);
        this.buttons.addListener(mouseListener);
        this.spellView = new SpellView();
        this.inventory = new Inventory();
        this.inventoryQuickBar = new InventoryQuickBar();
        this.spellViewExpanded = new SpellViewExpanded();
        this.expandButton = new ImageTextButton ("-", Skins.COMODORE_SKIN);
        this.expandButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getInventory().setVisible(!getInventory().isVisible());
                getSpellViewExpanded().setVisible(!getSpellViewExpanded().isVisible());
                if (getInventory().isVisible()) {
                    expandButton.setText("-");
                } else {
                    expandButton.setText("+");
                }

            }
        });

        add().top();
        add(buttons).top().right().row();
        add(inventory).padTop(PAD_TOP);
        add(inventoryQuickBar).padTop(PAD_TOP).right().row();
        add().top();
        add(expandButton).right().padTop(-10f);
    }

    @Override
    public void notify(State state) {
        switch (state) {
            case SPELLS:
                clear();
                add().top();
                add(buttons).top().right().row();
                add(spellViewExpanded).padTop(PAD_TOP).right();
                add(spellView).padTop(PAD_TOP).right().row();
                add();
                add(expandButton).padTop(-10f).right();
                expandButton.setVisible(spellView.isVisible());
                currentState = "SPELL";
                break;
            case INVENTORY:
                clear();
                add().top();
                add(buttons).top().right().row();
                add(inventory).padTop(PAD_TOP).right();
                add(inventoryQuickBar).padTop(PAD_TOP).right().row();
                add();
                add(expandButton).padTop(-10f).right();
                expandButton.setVisible(inventoryQuickBar.isVisible());
                currentState = "INVENTORY";
                break;
        }
    }

    public boolean isOver() {
        return getInventory().isOver() || getSpellView().isOver() || getInventoryQuickBar().isOver() || getSpellViewExpanded().isOver() || mouseListener.isOver() || expandButton.isOver();
    }

    public void toggle() {
        buttons.toggle();
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected SpellView getSpellView() {
        return spellView;
    }

    public InventoryQuickBar getInventoryQuickBar() {
        return inventoryQuickBar;
    }

    public SpellViewExpanded getSpellViewExpanded() {
        return spellViewExpanded;
    }


    public SwitchButtons getButtons() {
        return buttons;
    }

    public void scrolled(int amount) {
        if (getInventory().isOver()) {
            getInventory().scrolled(amount);
        } else if (getSpellView().isOver()) {
            // TODO
        }
    }

    public void setExpandButtonVisible() {
        expandButton.setVisible(!expandButton.isVisible());
    }

    public String getState() {
        return currentState;
    }
}
