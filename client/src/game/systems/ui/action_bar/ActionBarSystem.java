package game.systems.ui.action_bar;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Actor;
import entity.character.info.Bag;
import entity.character.info.SpellBook;
import game.systems.ui.UserInterfaceContributionSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.ui.action_bar.systems.SpellSystem;
import game.ui.ActionBar;

@Wire
public class ActionBarSystem extends UserInterfaceContributionSystem {

    private InventorySystem inventorySystem;
    private SpellSystem spellSystem;

    public ActionBarSystem() {
        super(Aspect.one(Bag.class, SpellBook.class));
    }

    @Override
    protected void calculate(int entityId) {

    }

    @Override
    public Actor getActor() {
        return new ActionBar();
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
