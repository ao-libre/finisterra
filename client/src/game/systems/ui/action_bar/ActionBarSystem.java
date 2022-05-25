package game.systems.ui.action_bar;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import component.entity.character.info.Bag;
import component.entity.character.info.SpellBook;
import game.systems.PlayerSystem;
import game.systems.screen.MouseSystem;
import game.systems.ui.UserInterfaceContributionSystem;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.systems.ui.action_bar.systems.SpellSystem;
import game.ui.SwitchButtons;
import game.ui.WidgetFactory;
import game.utils.Skins;

import static com.artemis.E.E;

@Wire
public class ActionBarSystem extends UserInterfaceContributionSystem {

    private InventorySystem inventorySystem;
    private SpellSystem spellSystem;
    private MouseSystem mouseSystem;
    private PlayerSystem playerSystem;

    private Actor actionBar;
    private Label goldLabel;

    public ActionBarSystem() {
        super(Aspect.one(Bag.class, SpellBook.class));
    }

    @Override
    public void calculate(int entityId) {
        inventorySystem.calculate(entityId);
        spellSystem.calculate(entityId);
        Table actionBar = WidgetFactory.createMainWindow();
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

        actionBar.add(buttons).top().padLeft(5).padRight(10).height(45).padTop(-8).growX().row();

        /* Inventary and spellbook  */
        Stack stack = new Stack();
        E e = E(entityId);
        if (e.hasBag()) { // add inventory
            stack.add(inventorySystem.getActor());
        }
        if (e.hasSpellBook()) { // add spellbook
            stack.add(spellSystem.getActor());
        }
        actionBar.add(stack).top().grow().row();
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

    public void updateGoldLabel(int goldCount) {
        goldLabel.setText(String.valueOf(goldCount));
    }

}
