package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.utils.Resources;
import game.utils.Skins;
import shared.model.Spell;

public abstract class SpellSlotUI extends ImageButton {

    static final int SIZE = 64;
    private static final float ICON_ALPHA = 0.5f;
    private static final Drawable selection = Skins.COMODORE_SKIN.getDrawable("slot-selected2");
    private Spell spell;
    private Texture graphic;
    private boolean selected;

    SpellSlotUI() {
        super(Skins.COMODORE_SKIN, "icon-container");
        addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                onSpellClick(SpellSlotUI.this);
            }
        });
    }

    public boolean isEmpty() {
        return spell != null;
    }

    public void setSpell(Spell spell) {
        if (spell.equals(this.spell)) return;
        this.spell = spell;
        if (graphic != null) {
            graphic.dispose();
        }
        this.graphic = spell != null ? new Texture(Gdx.files.local(Resources.GAME_SPELL_ICONS_PATH + spell.getId() + ".png")) : null;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (spell == null) {
            return;
        }
        drawSpell(batch);
        if (selected) drawSelection(batch);
    }

    private void drawSelection(Batch batch) {
        selection.draw(batch, getX(), getY(), SIZE, SIZE);
    }

    private void drawSpell(Batch batch) {
        if (graphic != null) {
            Color current = new Color(batch.getColor());
            batch.setColor(current.r, current.g, current.b, ICON_ALPHA);
            batch.draw(graphic, getX() + 1, getY() + 1);
            batch.setColor(current);
        }
    }

    public Spell getSpell() {
        return spell;
    }

    abstract public void onSpellClick(SpellSlotUI spellSlotUI);

}
