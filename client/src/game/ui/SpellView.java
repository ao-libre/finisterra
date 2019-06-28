package game.ui;

import com.artemis.E;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import game.handlers.ObjectHandler;
import game.handlers.SpellHandler;
import game.screens.GameScreen;
import game.utils.Colors;
import game.utils.Cursors;
import game.utils.Skins;
import shared.model.Spell;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import static com.artemis.E.E;

public class SpellView extends Window {

    public static final int MAX_SPELLS = 6;
    public Optional<Spell> toCast = Optional.empty();

    public SpellView() {
        super("", Skins.COMODORE_SKIN, "inventory");
    }

    private void changeCursor() {
        GUI.getConsole().addInfo("Haz click para lanzar el hechizo");
        Cursors.setCursor("select");
    }

    public void updateSpells() {
        clear();
        SpellHandler spellHandler = GameScreen.getWorld().getSystem(SpellHandler.class);
        final Spell[] spells = spellHandler.getSpells();
        Arrays.sort(spells, getComparator());
        Arrays.stream(spells).forEach(spell -> {
            add(new SpellSlot(this, spell)).width(SpellSlot.SIZE).height(SpellSlot.SIZE).row();
            if (!spells[spells.length - 1].equals(spell)) {
                add(new Image(getSkin().getDrawable("separator"))).row();
            }
        });
        setVisible(E(GameScreen.getPlayer()).getMana().max > 0);
    }

    void preparedToCast(Spell spell) {
        toCast = Optional.of(spell);
        changeCursor();
    }

    private Comparator<Spell> getComparator() {
        return (s1, s2) -> s2.getFxGrh() - s1.getFxGrh();
    }

    public boolean isOver() {
        return Stream.of(getChildren().items).filter(SpellSlot.class::isInstance).map(SpellSlot.class::cast).anyMatch(SpellSlot::isOver);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int player = GameScreen.getPlayer();
        Color backup = batch.getColor();
        if (player >= 0) {
            E e = E(player);
            if (e != null && e.hasAttack()) {
                batch.setColor(Colors.COMBAT);
            }
        }
        super.draw(batch, parentAlpha);
        batch.setColor(backup);
    }
}
