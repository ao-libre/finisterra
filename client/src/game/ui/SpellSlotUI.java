package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.utils.Resources;
import game.utils.Skins;
import shared.model.Spell;

public abstract class SpellSlotUI extends Table {

    private static final float ICON_ALPHA = 0.5f;
    private static final Drawable selection = WidgetFactory.createDrawable(WidgetFactory.Drawables.INVENTORY_SLOT_SELECTION.name);
    private Spell spell;
    private Texture graphic;
    private boolean selected;
    private Image image;
    private Label label;

    SpellSlotUI() {
        super(Skins.CURRENT.get());
        image = new Image();
        label = WidgetFactory.createSpellLabel("(None)");
        label.setAlignment(Align.left);
        add(image).right().width(16).height(16);
        add(label).padLeft(5).grow();
        addListener(new ClickListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    onSpellClick(SpellSlotUI.this);
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean result = super.touchDown(event, x, y, pointer, button);
                onSpellClick(SpellSlotUI.this);
                return result;
            }
        });
    }

    public boolean isEmpty() {
        return spell != null;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (selected) drawSelection(batch);
        super.draw(batch, parentAlpha);
    }

    private void drawSelection(Batch batch) {
        selection.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        if (spell != null && spell.equals(this.spell)) return;
        this.spell = spell;
        if (graphic != null) {
            graphic.dispose();
        }
        if (this.spell != null) {
            this.graphic = new Texture(Gdx.files.local(Resources.GAME_SPELL_ICONS_PATH + spell.getId() + ".png"));
            image.setDrawable(new TextureRegionDrawable(graphic));
        } else {
            image.setDrawable(null);
        }
        label.setText(this.spell == null ? "(None)" : this.spell.getName());
    }

    abstract public void onSpellClick(SpellSlotUI spellSlotUI);

}
