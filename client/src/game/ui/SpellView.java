package game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.utils.Skins;
import shared.model.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SpellView extends Table {

    private static final int MAX_SPELLS = 6;
    public Optional<Spell> toCast = Optional.empty();
    public Optional<Spell> selected = Optional.empty();
    private ImageButton castButton;
    private Window spellTable;
    private List<SpellSlot> slots = new ArrayList<>(MAX_SPELLS);
    private int base;

    public SpellView() {
        super(Skins.COMODORE_SKIN);
        spellTable = new Window("", Skins.COMODORE_SKIN, "inventory");
        for (int i = 0; i < MAX_SPELLS; i++) {
            SpellSlot slot = new SpellSlot(this, null);
            slots.add(slot);
            spellTable.add(slot).width(SpellSlot.SIZE).height(SpellSlot.SIZE).row();
            if (i < MAX_SPELLS - 1) {
                spellTable.add(new Image(getSkin().getDrawable("separator"))).row();
            }
        }
        castButton = createCastButton();
        add(castButton).padRight(-25f);
        add(spellTable).right();
        spellTable.toFront();
    }

    /**
     * Cambia el cursor al seleccionar un hechizo.
     */
    private void changeCursor() {
//        WorldUtils.getWorld().ifPresent(world -> {
//            world.getSystem(UserInterfaceSystem.class).getConsole().addInfo("Haz click para lanzar el hechizo");
//            world.getSystem(UserInterfaceSystem.class).getInventory().cleanShoot();
//        });
//        Cursors.setCursor("select");
    }

    /**
     * Actualiza la lista de hechizos en la UI.
     */
    public void updateSpells() {
//        WorldUtils.getWorld().ifPresent(world -> {
//            SpellsSystem spellsSystem = world.getSystem(SpellsSystem.class);
//            Spell[] spells = spellsSystem.getSpells();
//            Spell[] spellsToShow = new Spell[MAX_SPELLS];
//            System.arraycopy(spells, base, spellsToShow, 0, Math.min(MAX_SPELLS, spells.length));
//            for (int i = 0; i < MAX_SPELLS; i++) {
//                slots.get(i).setSpell(spellsToShow[i]);
//            }
//        });
    }

    public void addSpelltoSpellview(Spell spell, int slotPosition) {
        slots.get(slotPosition).setSpell(spell);
    }

    /**
     * Interfaz de Usuario: Crea el boton para lanzar el hechizo.
     *
     * @return staff
     */

    private ImageButton createCastButton() {
        ImageButton staff = new ImageButton(Skins.COMODORE_SKIN, "staff");
        staff.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected.ifPresent(spell -> preparedToCast(spell));
                super.clicked(event, x, y);
            }
        });
        return staff;
    }

    void selected(Spell spell) {
        selected = Optional.ofNullable(spell);
    }

    void preparedToCast(Spell spell) {
        toCast = Optional.ofNullable(spell);
        changeCursor();
    }

    public boolean isOver() {
        return Stream.of(spellTable.getChildren().items)
                .filter(SpellSlot.class::isInstance)
                .map(SpellSlot.class::cast)
                .anyMatch(SpellSlot::isOver) || castButton.isOver();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        int player = GameScreen.getPlayer();
//        Color backup = batch.getColor();
//        if (player >= 0) {
//            E e = E(player);
//            if (e != null && e.hasAttack()) {
//                batch.setColor(Colors.COMBAT);
//            }
//        }
//        super.draw(batch, parentAlpha);
//        batch.setColor(backup);
    }

    public void cleanCast() {
        toCast = Optional.empty();
        castButton.setChecked(false);
    }
}
