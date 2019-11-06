package game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.ui.SwitchButtons.ActionSwitchListener;
import game.ui.SwitchButtons.State;
import game.utils.Skins;


public class ActionBar extends Table implements ActionSwitchListener {

    private static final float PAD_TOP = -17f;
    private final ClickListener mouseListener;
    private SwitchButtons buttons;
    private SpellView spellView;
    private SpellViewExpanded spellViewExpanded;
    private Inventory inventory;
    private InventoryQuickBar inventoryQuickBar;
    private ImageTextButton expandButton;
    private String currentState = "INVENTORY";

    ActionBar() {
        super(Skins.COMODORE_SKIN);
        mouseListener = new ClickListener();
        buttons = new SwitchButtons();
        buttons.addListener(this);
        buttons.addListener(mouseListener);
        spellView = new SpellView();
        inventory = new Inventory();
        inventoryQuickBar =new InventoryQuickBar ();
        spellViewExpanded = new SpellViewExpanded ();
        expandButton = new ImageTextButton ("-", Skins.COMODORE_SKIN, "inventory-expand-collapse");
        expandButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getInventory ().setVisible(!getInventory ().isVisible ());
                getSpellViewExpanded ().setVisible ( !getSpellViewExpanded ().isVisible () );
                if (getInventory ().isVisible ()){
                    expandButton.setText ( "-" );
                }else{
                    expandButton.setText ( "+" );
                }

            }
        });

        add().top();
        add(buttons).top().right ().row();
        add(inventory).padTop(PAD_TOP);
        add( inventoryQuickBar ).padTop(PAD_TOP).right ().row ();
        add().top();
        add( expandButton ).right ().padTop (-10f);
    }

    @Override
    public void notify(State state) {
        switch (state) {
            case SPELLS:
                clear();
                add().top();
                add(buttons).top().right().row();
                add( spellViewExpanded ).padTop(PAD_TOP).right ();
                add(spellView).padTop(PAD_TOP).right().row ();
                add();
                add( expandButton ).padTop (-10f).right ();
                expandButton.setVisible(spellView.isVisible ());
                currentState = "SPELL";
                break;
            case INVENTORY:
                clear();
                add().top();
                add(buttons).top().right ().row();
                add(inventory).padTop(PAD_TOP).right ();
                add( inventoryQuickBar ).padTop(PAD_TOP).right ().row ();
                add();
                add( expandButton ).padTop (-10f).right () ;
                expandButton.setVisible(inventoryQuickBar.isVisible ());
                currentState = "INVENTORY";
                break;
        }
    }

    public boolean isOver() {
        return getInventory().isOver() || getSpellView().isOver() || getInventoryQuickBar ().isOver() || getSpellViewExpanded ().isOver () || mouseListener.isOver() || expandButton.isOver ();
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

    public SpellViewExpanded getSpellViewExpanded(){
        return spellViewExpanded;
    }


    public void scrolled(int amount) {
        if (getInventory().isOver()) {
            getInventory().scrolled(amount);
        } else if (getSpellView().isOver()) {
            // TODO
        }
    }

    public void setExpandButtonVisible() {
        expandButton.setVisible (!expandButton.isVisible ());
    }

    public String getState() {
        return currentState;
    }
}
