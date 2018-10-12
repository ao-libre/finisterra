package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.systems.interactions.InventorySystem;
import ar.com.tamborindeguy.client.utils.Skins;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.Optional;

import static com.artemis.E.E;

public class Inventory extends Window {

    private static final int SIZE = 20;
    public static final int COLUMNS = 5;
    public static final float ZOOM = 1.35f;

    private ArrayList<Slot> slots;
    private Optional<Slot> selected = Optional.empty();

    public Inventory() {
        super("Inventory", Skins.COMODORE_SKIN, "black");
        setMovable(true);
        padTop(15 * ZOOM);
        slots = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            Slot newSlot = new Slot();
            slots.add(newSlot);
            newSlot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selected.ifPresent(slot -> slot.setSelected(false));
                    newSlot.setSelected(true);
                    selected = Optional.of(newSlot);
                    if (getTapCount() >= 2) {
                        newSlot.toggleEquipped();
                        InventorySystem.equip(newSlot.getObjId(), newSlot.isEquipped());
                    }
                }


            });

        }
    }

    public void fillUserInventory(int player) {
        ArrayList<entity.character.info.Inventory.Item> userItems = E(player).getInventory().items;
        userItems.forEach(item -> {
            if (item != null) {
                Slot slot = slots.get(userItems.indexOf(item));
                slot.setObjId(item.objId);
                slot.setCount(item.count);
            }
        });
        for(int i = 0; i < SIZE; i++) {
            add(slots.get(i)).width(Slot.SIZE * ZOOM).height(Slot.SIZE * ZOOM);
            if (i + 1 >= COLUMNS && (i+1) % COLUMNS == 0) {
                row();
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = parentAlpha;
        if (!mouseOnInventory()) {
            alpha = 0.5f;
        }
        super.draw(batch, alpha);
    }

    private boolean mouseOnInventory() {
        boolean xIN = Gdx.input.getX() > getX() && Gdx.input.getX() < getX() + getWidth();
        boolean yIN = Gdx.input.getY() > getY() && Gdx.input.getY() < getY() + getHeight();
        return yIN && xIN;
    }
}
