package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.utils.Resources;
import game.utils.Skins;
import shared.model.Spell;

public class SpellSlot extends ImageButton {

    public static final float ICON_ALPHA = 0.5f;
    static final int SIZE = 64;
    private static Drawable selection = Skins.COMODORE_SKIN.getDrawable("slot-selected2");
    private final SpellView spellView;
    private Spell spell;
    private final ClickListener clickListener;

    private Texture icon;
    private Tooltip tooltip;

    SpellSlot(SpellView spellView, Spell spell) {
        super(Skins.COMODORE_SKIN, "icon-container");
        this.spellView = spellView;
        clickListener = new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                onClick();
            }
        };
        addListener(clickListener);
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
        if(spell == null) {
            return;
        }
        if(tooltip != null) {
            removeListener(tooltip);
        }
        tooltip = getTooltip(spell);
        addListener(tooltip);
    }

    private Tooltip getTooltip(Spell spell) {
        Actor content = createTooltipContent(spell); 
        return new Tooltip(content);
    }

    private Actor createTooltipContent(Spell spell) {
        Table table = new Window("", Skins.COMODORE_SKIN);
        String name = spell.getName();
        int requiredMana = spell.getRequiredMana();
//        table.add(new Label(name, Skins.COMODORE_SKIN, "title-no-background")).prefWidth(200).row();
//        table.add(new Label("Mana: " + requiredMana, Skins.COMODORE_SKIN, "desc-no-background")).pad(20).left();
        table.setHeight(100);
        return table;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (spell == null) {
            return;
        }
        drawSpell(batch);
        spellView.selected.filter(sp -> sp.equals(spell)).ifPresent(sp -> drawSelection(batch));
    }

    private void drawSelection(Batch batch) {
        selection.draw(batch, getX(), getY(), SIZE, SIZE);
    }

    private void drawSpell(Batch batch) {
        Texture graphic = getSpellIcon();
        Color current = new Color(batch.getColor());
        batch.setColor(current.r, current.g, current.b, ICON_ALPHA);
        batch.draw(graphic, getX() + 1, getY() + 1);
        batch.setColor(current);
    }

    private void onClick() {
        spellView.selected(spell);
    }

    public boolean isOver() {
        return clickListener != null && clickListener.isOver();
    }

    private Texture getSpellIcon() {
        if (icon == null) {
            icon = new Texture(Gdx.files.local(Resources.GAME_SPELL_ICONS_PATH + spell.getFxGrh() + ".png"));
        }
        return icon;
    }

}
