package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.utils.Resources;
import shared.model.Spell;

public class SpellSlot extends Actor {

    public static final float ICON_ALPHA = 0.5f;
    static final int SIZE = 64;
    public static Texture selection = new Texture(Gdx.files.local("data/ui/images/slot-selection.png"));
    public static Texture background = new Texture(Gdx.files.local("data/ui/images/table-background.png"));
    private final SpellView spellView;
    private final Spell spell;
    private final ClickListener clickListener;

    private Texture icon;

    SpellSlot(SpellView spellView, Spell spell) {
        this.spellView = spellView;
        this.spell = spell;
        clickListener = new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                onClick();
            }
        };
        addListener(clickListener);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX(), getY());
        drawSpell(batch);
        spellView.toCast.filter(sp -> sp.equals(spell)).ifPresent(sp -> drawSelection(batch));
        if (isOver()) {
            // TODO draw spell name
        }
    }

    private void drawSelection(Batch batch) {
        batch.draw(selection, getX(), getY(), SIZE, SIZE);
    }

    private void drawSpell(Batch batch) {
        Texture graphic = getSpellIcon();
        Color current = new Color(batch.getColor());
        batch.setColor(current.r, current.g, current.b, ICON_ALPHA);
        batch.draw(graphic, getX() + 1, getY() + 1);
        batch.setColor(current);
    }

    private void onClick() {
        spellView.preparedToCast(spell);
    }

    public boolean isOver() {
        return clickListener.isOver();
    }

    private Texture getSpellIcon() {
        if (icon == null) {
            icon = new Texture(Gdx.files.local(Resources.GAME_SPELL_ICONS_PATH + spell.getFxGrh() + ".png"));
        }
        return icon;
    }

}
