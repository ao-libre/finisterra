package game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import component.entity.character.info.SpellBook;
import game.utils.Skins;
import shared.model.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SpellBookUI extends Table {

    private static final int MAX_SPELLS = 6;
    public Optional<SpellSlotUI> selected = Optional.empty();
    private List<SpellSlotUI> slots = new ArrayList<>(MAX_SPELLS);

    public SpellBookUI() {
        super(Skins.COMODORE_SKIN);
        Window spellTable = new Window("", Skins.COMODORE_SKIN, "inventory");
        for (int i = 0; i < MAX_SPELLS; i++) {
            SpellSlotUI slot = new SpellSlotUI() {
                @Override
                public void onSpellClick(SpellSlotUI spellSlotUI) {
                    selected.ifPresent(slot -> slot.setSelected(false));
                    selected = Optional.ofNullable(spellSlotUI);
                    if (spellSlotUI != null) {
                        spellSlotUI.setSelected(true);
                    }
                }
            };
            slots.add(slot);
            spellTable.add(slot).width(SpellSlot.SIZE).height(SpellSlot.SIZE).row();
            if (i < MAX_SPELLS - 1) {
                spellTable.add(new Image(getSkin().getDrawable("separator"))).row();
            }
        }
        add(spellTable).right();
        spellTable.toFront();
    }

    public void update(SpellBook spellBook, int base) {
        Integer[] spells = spellBook.spells;
        for (int i = 0; i < MAX_SPELLS; i++) {
            if (i < spells.length) {
                slots.get(i).setSpell(getSpell(spells[i + base]));
            }
        }
    }

    public void castClick(){
        selected.ifPresent(spell -> onCastClicked(spell));
    }


    protected abstract void onCastClicked(SpellSlotUI spell);

    protected abstract Spell getSpell(Integer spellId);

//    @Override
//    public void draw(Batch batch, float parentAlpha) {
////        int player = GameScreen.getPlayer();
////        Color backup = batch.getColor();
////        if (player >= 0) {
////            E e = E(player);
////            if (e != null && e.hasAttack()) {
////                batch.setColor(Colors.COMBAT);
////            }
////        }
//        super.draw(batch, parentAlpha);
////        batch.setColor(backup);
//    }

}
