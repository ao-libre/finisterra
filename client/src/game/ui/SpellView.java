package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import game.handlers.SpellHandler;
import game.screens.GameScreen;
import game.utils.Cursors;
import game.utils.Skins;
import shared.model.Spell;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.artemis.E.E;

public class SpellView extends Window {

    public static final int MAX_SPELLS = 6;
    public Optional<Spell> toCast = Optional.empty();

    public SpellView() {
        super("", Skins.COMODORE_SKIN, "no-background");
    }

    private void changeCursor() {
        Cursors.setCursor("select");
    }

    public void updateSpells() {
        clear();
        final Spell[] spells = SpellHandler.getSpells();
        Arrays.sort(spells, getComparator());
        Arrays.stream(spells).forEach(spell -> add(new SpellSlot(this, spell)).width(SpellSlot.SIZE).height(SpellSlot.SIZE).row());
        setVisible(E(GameScreen.getPlayer()).manaMax() > 0);
    }

    public void preparedToCast(Spell spell) {
        toCast = Optional.of(spell);
        changeCursor();
    }

    private Comparator<Spell> getComparator() {
        return (s1, s2) -> s2.getFxGrh() - s1.getFxGrh();
    }

    public boolean isOver() {
        return Stream.of(getChildren().items).filter(Objects::nonNull).map(SpellSlot.class::cast).anyMatch(SpellSlot::isOver);
    }

}
