package game.systems.ui.action_bar;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.minlog.Log;
import component.entity.character.info.Bag;
import component.entity.character.info.SpellBook;
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
    private Actor actionBar;

    public ActionBarSystem() {
        super(Aspect.one(Bag.class, SpellBook.class));
    }

    @Override
    public void calculate(int entityId) {
        inventorySystem.calculate(entityId);
        spellSystem.calculate(entityId);
        Table actionBar = new Table(Skins.COMODORE_SKIN);
        Log.info("Creating Action Bar for component.entity: " + entityId);
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

        actionBar.add(buttons).top().row();

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
        inventorySystem.show();
    }

    public void showSpells() {
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
}
