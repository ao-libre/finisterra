package game.ui;

import com.artemis.E;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import game.handlers.SpellHandler;
import game.screens.GameScreen;
import game.utils.Colors;
import game.utils.Skins;
import game.utils.WorldUtils;
import shared.model.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.artemis.E.E;

public class EchisosCompletos extends Table {

    public static final int MAX_SPELLS = 25;
    public Optional<Spell> toCast = Optional.empty();
    public Optional<Spell> selected = Optional.empty();
    private Spell spell;
    private Window spellTable;
    private List<SpellSlotEC> slotsEC = new ArrayList<>(MAX_SPELLS);
    private int base;
    private int j = 1;

    public EchisosCompletos() {
        super(Skins.COMODORE_SKIN);
        spellTable = new Window("", Skins.COMODORE_SKIN, "inventory");
        for (int i = 0; i < MAX_SPELLS; i++) {
            SpellSlotEC slot = new SpellSlotEC(this, null);
            slotsEC.add(slot);

            if (j < 5) {
                spellTable.add(slot).width(SpellSlotEC.SIZE).height(SpellSlotEC.SIZE);
            } else {
                spellTable.add(slot).width(SpellSlotEC.SIZE).height(SpellSlotEC.SIZE).row();
                j = 0;
            }
            j++;
        }
        add(spellTable);
    }


    public void updateSpells() {

        WorldUtils.getWorld().ifPresent(world -> {
            SpellHandler spellHandler = world.getSystem( SpellHandler.class);
            Spell[] spells = spellHandler.getSpells();
            Spell[] spellsToShow = new Spell[MAX_SPELLS];
            System.arraycopy(spells, base, spellsToShow, 0, Math.min(MAX_SPELLS, spells.length));
            for (int i = 0; i < MAX_SPELLS; i++) {
                slotsEC.get(i).setSpell(spellsToShow[i]);
            }
        });


    }

    void selected(Spell spell) {
        selected = Optional.ofNullable(spell);
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
