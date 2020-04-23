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
    private ImageTextButton expandInventoryButton;
    private ImageButton castButton, shotButton;
    private Label goldLabel;

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

        actionBar.add(buttons).top().right().colspan(2).padRight(10).row();

        /*cast and shot*/
        Table buttonsTable = new Table();
        Stack buttonStack = new Stack();
        castButton = createCastButton();
        buttonStack.add(castButton);
        shotButton = createShotButton();
        buttonStack.add(shotButton);
        buttonsTable.add(buttonStack).right().row();
        expandInventoryButton = createExpandInventoryButton();
        buttonsTable.add(expandInventoryButton).right().width(50).height(50);
        actionBar.add(buttonsTable).padRight(-25f).width(100);

        /* Inventary and spellbook  */
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

        /*gold table*/
        Table goldTable = new Table();
        Cell<Image> goldIconCell = goldTable.add(WidgetFactory.createImage(new Texture(Gdx.files.local("data/ui/images/gold.png"))));
        goldIconCell.height(28).width(30).left();
        goldLabel = WidgetFactory.createLabel("");
        goldLabel.setText(String.valueOf(playerSystem.get().goldCount()));
        goldLabel.setColor(Color.GOLDENROD);
        goldLabel.setAlignment(Align.right);
        goldTable.add(goldLabel).height(28).fillY().right();
        actionBar.add(goldTable).colspan(2).right().padRight(10);

        this.actionBar = actionBar;
    }

    @Override
    public Actor getActor() {
        return actionBar;
    }

    public void showInventory() {
        spellSystem.hide();
        castButton.setVisible(false);
        shotButton.setVisible(true);
        expandInventoryButton.setVisible(true);
        inventorySystem.show();
    }

    public void showSpells() {
        if (inventorySystem.isExpanded()) {
            inventorySystem.toggleExpanded();
        }
        expandInventoryButton.setVisible(false);
        castButton.setVisible(true);
        shotButton.setVisible(false);
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

    private ImageTextButton createExpandInventoryButton() {
        expandInventoryButton = new ImageTextButton("", Skins.COMODORE_SKIN, "inventory-expand-collapse");
        expandInventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventorySystem.toggleExpanded();
            }
        });
        return expandInventoryButton;
    }

    private ImageButton createCastButton() {
        ImageButton staff = WidgetFactory.createImageStaffButton();
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
        Sprite shotSprite = new Sprite(new Texture(Gdx.files.local("data/graficos2x/16007.png")));
        shotSprite.rotate90(true);
        SpriteDrawable shotDrawable = new SpriteDrawable(shotSprite);
        ImageButton.ImageButtonStyle shotStile = new ImageButton.ImageButtonStyle();
        shotStile.up = Skins.COMODORE_SKIN.getDrawable("big-disc");
        shotStile.imageUp = shotDrawable.tint(Color.DARK_GRAY);
        shotStile.imageChecked = shotDrawable.tint(Color.GOLDENROD);

        ImageButton shotButton = WidgetFactory.createImageButton(shotStile);

        shotButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mouseSystem.shot();
            }
        });
        return shotButton;
    }

    public void clearShot() {
        shotButton.setChecked(false);
    }

    public void updateGoldLabel(int goldCount) {
        goldLabel.setText(String.valueOf(goldCount));
    }

}
