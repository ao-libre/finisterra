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
    private SwitchButtons relleno;
    private SpellView spellView;
    private SpellViewExpanded spellViewExpanded;
    private Inventory inventory;
    private InventoryQuickBar inventoryQuickBar;
    private ImageTextButton expandButton;

    ActionBar() {
        super(Skins.COMODORE_SKIN);
        mouseListener = new ClickListener();
        buttons = new SwitchButtons();
        buttons.addListener(this);
        buttons.addListener(mouseListener);
        relleno = new SwitchButtons (  );
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

        add(relleno).top();
        relleno.setVisible(false);
        add(buttons).top().right ().row();
        add(inventory).padTop(PAD_TOP);
        add( inventoryQuickBar ).padTop(PAD_TOP).right ().row ();
        add(relleno).top();
        add( expandButton ).right ().padTop (-10f);
    }

    @Override
    public void notify(State state) {
        switch (state) {
            case SPELLS:
                clear();
                add(relleno).top();
                relleno.setVisible(false);
                add(buttons).top().right().row();
                add( spellViewExpanded ).padTop(PAD_TOP).right ();
                add(spellView).padTop(PAD_TOP).right().row ();
                add(relleno);
                add( expandButton ).padTop (-10f).right ();
                expandButton.setVisible(spellView.isVisible ());
                break;
            case INVENTORY:
                clear();
                add(relleno).top();
                relleno.setVisible(false);
                add(buttons).top().right ().row();
                add(inventory).padTop(PAD_TOP).right ();
                add( inventoryQuickBar ).padTop(PAD_TOP).right ().row ();
                add(relleno);
                add( expandButton ).padTop (-10f).right () ;
                expandButton.setVisible(inventoryQuickBar.isVisible ());
                break;
        }
    }

    public boolean isOver() {
        return getInventory().isOver() || getSpellView().isOver() || getInventoryQuickBar ().isOver()  || mouseListener.isOver();
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
}
