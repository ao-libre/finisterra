package game.systems.ui.action_bar;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.minlog.Log;
import component.entity.character.info.Bag;
import component.entity.character.info.SpellBook;
import game.systems.screen.MouseSystem;
import game.systems.ui.UserInterfaceContributionSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.ui.action_bar.systems.SpellSystem;
import game.ui.SwitchButtons;
import game.utils.Skins;

import static com.artemis.E.E;

@Wire
public class ActionBarSystem extends UserInterfaceContributionSystem {

    private InventorySystem inventorySystem;
    private SpellSystem spellSystem;
    private MouseSystem mouseSystem;
    private Actor actionBar;
    private ImageTextButton expandInventoryButton;
    private ImageButton castButton, shotButton;

    public ActionBarSystem() {
        super(Aspect.one(Bag.class, SpellBook.class));
    }

    @Override
    public void calculate(int entityId) {
        inventorySystem.calculate(entityId);
        spellSystem.calculate(entityId);
        Table actionBar = new Table(Skins.COMODORE_SKIN);
        Log.debug("Creating Action Bar for component.entity: " + entityId);
        SwitchButtons buttons = new SwitchButtons();
        buttons.addListener(state -> {
            switch (state) {
                case SPELLS:
                    showSpells();
                    break;
                case INVENTORY:
                    showInventory();
                    break;
            }
        });

        actionBar.add();
        actionBar.add(buttons).top().right().row();
        Table buttonsTable = new Table();
        Stack buttonStack = new Stack();
        castButton = createCastButton();
        buttonStack.add( castButton );
        shotButton = createShotButton();
        buttonStack.add( shotButton );
        buttonsTable.add( buttonStack ).right().row();
        expandInventoryButton = createExpandInventoryButton();
        buttonsTable.add( expandInventoryButton ).right().width(50).height(50);
        actionBar.add(buttonsTable).padRight( -25f );

        Stack stack = new Stack();
        E e = E(entityId);
        if (e.hasBag()) {
            // add inventory
            stack.add(inventorySystem.getActor());
        }
        if (e.hasSpellBook()) {
            // add spellbook
            stack.add(spellSystem.getActor());
        }
        actionBar.add(stack).top().right().row();
        this.actionBar = actionBar;
    }

    @Override
    public Actor getActor() {
        return actionBar;
    }

    public void showInventory() {
        spellSystem.hide();
        castButton.setVisible( false );
        shotButton.setVisible( true );
        expandInventoryButton.setVisible( true );
        inventorySystem.show();
    }

    public void showSpells() {
        expandInventoryButton.setVisible( false );
        castButton.setVisible( true);
        shotButton.setVisible( false );
        spellSystem.show();
        inventorySystem.hide();
    }

    public void toggle() {
        if (spellSystem.isVisible()) {
            showInventory();
        } else {
            showSpells();
        }
    }
    private ImageTextButton createExpandInventoryButton(){
        expandInventoryButton = new ImageTextButton("",Skins.COMODORE_SKIN, "inventory-expand-collapse" );
        expandInventoryButton.addListener( new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventorySystem.toggleExpanded();
            }
        });
        return expandInventoryButton;
    }
    private ImageButton createCastButton() {
        ImageButton staff = new ImageButton(Skins.COMODORE_SKIN, "staff");
        staff.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                spellSystem.castClick();
            }
        });
        return staff;
    }

    public void clearCast() {
        castButton.setChecked(false);
    }

    private ImageButton createShotButton() {
        ImageButton shotButton = new ImageButton( Skins.COMODORE_SKIN, "disc" );
        shotButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mouseSystem.shot();
            }
        });
        return shotButton;
    }
}
