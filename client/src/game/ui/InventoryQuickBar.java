package game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import entity.character.info.Inventory.Item;
import game.screens.GameScreen;
import game.utils.Skins;
import shared.network.inventory.ItemActionRequest;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static com.artemis.E.E;

public class InventoryQuickBar extends Window {

    private static final int COLUMNS = 6;
    private static final int ROWS = 1;
    private static final int SIZE = COLUMNS * ROWS;
    private final ClickListener mouseListener;

    private ArrayList<Slot> quickInventorySlot; //object

    private Optional<Slot> selected = Optional.empty();
    private ArrayList<Integer> gBases; //index of inventoryQuickBar

    InventoryQuickBar() {
        super("", Skins.COMODORE_SKIN, "inventory");
        setMovable(false);
        quickInventorySlot = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            Slot nuevoSlot = new Slot();
            quickInventorySlot.add(nuevoSlot);
            add(quickInventorySlot.get(i)).width(Slot.SIZE).height(Slot.SIZE).row();
            if (i < SIZE - 1) {
                add(new Image(getSkin().getDrawable("separator"))).row();
            }

        }
        mouseListener = getMouseListener();
        addListener(mouseListener);
        gBases = new ArrayList<Integer>();
        for (int i = 0; i < 6; i++) {
            gBases.add(i);
        }
    }

    private ClickListener getMouseListener() {
        return new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected.ifPresent(slot -> slot.setSelected(false));
                selected = getSlot(x, y);
                selected.ifPresent(slot -> {
                    slot.setSelected(true);
                    slot.getItem().ifPresent(item -> {
                        GameScreen.getClient().sendToAll(new ItemActionRequest(gBases.get(quickInventorySlot.indexOf(slot))));
                        GameScreen.world.getSystem(GUI.class).getInventory().isBowORArrow(slot);
                    });
                });
            }

            private Optional<Slot> getSlot(float x, float y) {
                return Stream.of(getChildren().items)
                        .filter(Slot.class::isInstance)
                        .filter(actor -> {
                            if (x > actor.getX() && x < actor.getWidth() + actor.getX()) {
                                return y > actor.getY() && y < actor.getHeight() + actor.getY();
                            }
                            return false;
                        })
                        .map(Slot.class::cast).findFirst();
            }

            final <T> void swap(T[] a, int i, int j) {
                T t = a[i];
                a[i] = a[j];
                a[j] = t;
            }

        };
    }


    public void addItemsIQB(int base, int x) {
        Item[] userItems = E(GameScreen.getPlayer()).getInventory().items;
        Item item = base < userItems.length ? userItems[base] : null;

        if (x > 0 && x < 6) {
            quickInventorySlot.get(x).setItem(item);
            gBases.set(x, base);
            x++;
        } else {
            x = 0;
            quickInventorySlot.get(x).setItem(item);
            gBases.set(x, base);
            x++;
        }
    }

    public int getGBases(int x) {
        return gBases.get(x);
    }

    public Optional<Slot> getSelected() {
        return selected;
    }

    public int selectedIndex() {
        assert (selected.isPresent());
        return quickInventorySlot.indexOf(selected.get());
    }

    public boolean isOver() {
        return mouseListener.isOver();
    }

}
