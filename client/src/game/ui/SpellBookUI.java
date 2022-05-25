package game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import component.entity.character.info.SpellBook;
import game.utils.Skins;
import shared.model.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SpellBookUI extends Table {

    private static final int MAX_SPELLS = SpellBook.SIZE;
    public Optional<SpellSlotUI> selected = Optional.empty();
    private final List<SpellSlotUI> slots = new ArrayList<>(MAX_SPELLS);

    public SpellBookUI() {
        super(Skins.CURRENT.get());
        Table spellTable = WidgetFactory.createInventoryWindow();
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
            spellTable.add(slot).growX().row();
        }
        final ScrollPane scrollPane = new ScrollPane(spellTable);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setFlickScroll(false);
        scrollPane.setOverscroll(false, false);
        add(scrollPane).maxHeight(200).growX();
        Table buttons = new Table();
        ImageButton moveUp = WidgetFactory.createImageButton(WidgetFactory.ImageButtons.ARROW_UP);
        moveUp.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                swap(-1);
            }
        });
        buttons.add(moveUp).row();
        buttons.add(WidgetFactory.createLineImage()).row();
        ImageButton moveDown = WidgetFactory.createImageButton(WidgetFactory.ImageButtons.ARROW_DOWN);
        moveDown.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                swap(1);
            }
        });
        buttons.add(moveDown).row();
        add(buttons);
        row();
        spellTable.toFront();
        TextButton castButton = WidgetFactory.createMagicTextButton("Lanzar");
        castButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                castClick();
            }
        });
        add(castButton).growX().padLeft(-2).colspan(2);
    }

    public void update(SpellBook spellBook, int base) {
        Integer[] spells = spellBook.spells;
        for (int i = 0; i < MAX_SPELLS; i++) {
            if (i < spells.length) {
                slots.get(i).setSpell(getSpell(spells[i + base]));
            }
        }
    }
    private void swap(int i) {
        selected.ifPresent(slot -> {
            int currentIndex = slots.indexOf(slot);
            if (currentIndex + i < MAX_SPELLS && currentIndex + i >= 0) {
                swap(currentIndex, currentIndex + i);
            }
        });
    }

    private void swap(int a, int b) {
        // todo notify change
        Spell spellB = slots.get(b).getSpell();
        slots.get(b).setSpell(slots.get(a).getSpell());
        slots.get(a).setSpell(spellB);
        slots.get(a).setSelected(false);
        slots.get(b).setSelected(true);
        selected = Optional.ofNullable(slots.get(b));
    }

    public void castClick() {
        selected.ifPresent(this::onCastClicked);
    }

    protected abstract void onCastClicked(SpellSlotUI spell);

    protected abstract Spell getSpell(Integer spellId);

}
