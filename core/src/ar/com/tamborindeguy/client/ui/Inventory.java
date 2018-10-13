package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.systems.interactions.InventorySystem;
import ar.com.tamborindeguy.client.utils.Skins;
import ar.com.tamborindeguy.objects.types.Obj;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import entity.character.info.Inventory.Item;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static com.artemis.E.E;

public class Inventory extends Window {

    public static final int COLUMNS = 5;
    public static final int ROWS = 4;
    private static final int SIZE = COLUMNS * ROWS;
    public static final float ZOOM = 1.35f;

    private ArrayList<Slot> slots;
    private Optional<Slot> selected = Optional.empty();
    private Optional<Slot> dragging = Optional.empty();
    private Optional<Slot> origin = Optional.empty();

    public Inventory() {
        super("Inventory", Skins.COMODORE_SKIN, "black");
        setMovable(true);
        padTop(15 * ZOOM);
        slots = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            Slot newSlot = new Slot();
            slots.add(newSlot);
            add(slots.get(i)).width(Slot.SIZE * ZOOM).height(Slot.SIZE * ZOOM);
            if (i + 1 >= COLUMNS && (i + 1) % COLUMNS == 0) {
                row();
            }
        }
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected.ifPresent(slot -> slot.setSelected(false));
                selected = getSlot(x, y);
                selected.ifPresent(slot -> {
                    slot.setSelected(true);
                    slot.getItem().ifPresent(item -> {
                        if (getTapCount() >= 2) {
                        item.action();
                            // TODO move to server
                            if (item.equipped) {
                                unequip(slots);
                            }
                            InventorySystem.equip(item.objId, item.equipped);
                        }
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

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean result = super.touchDown(event, x, y, pointer, button);
                if (result) {
                    origin = getSlot(x, y);
                }
                return result;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                    dragging = origin;
                } else {
                    dragging = Optional.empty();
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (dragging.isPresent()) {
                    Item[] userItems = E(GameScreen.getPlayer()).getInventory().items;
                    // notify server
                    getSlot(x, y).ifPresent(target -> {
                        int targetIndex = slots.indexOf(target);
                        if (userItems[targetIndex] != null) {
                            swap(userItems, slots.indexOf(dragging.get()), targetIndex);
                        } else {
                            userItems[targetIndex] = userItems[slots.indexOf(dragging.get())];
                        }
                        updateUserInventory();
                    });

                }
                dragging = Optional.empty();
            }

            final <T> void swap(T[] a, int i, int j) {
                T t = a[i];
                a[i] = a[j];
                a[j] = t;
            }

            private void unequip(ArrayList<Slot> slots) {
                slots.stream().filter(slot -> !slot.equals(selected.get())).forEach(slot -> slot.getItem().ifPresent(item -> {
                    Item equippedItem = selected.get().getItem().get();
                    if (sameKind(equippedItem, item)) {
                        item.equipped = false;
                        InventorySystem.equip(item.objId, false);
                    }
                }));
            }

            private boolean sameKind(Item equippedItem, Item item) {
                Optional<Obj> object = ObjectHandler.getObject(equippedItem.objId);
                Optional<Obj> object1 = ObjectHandler.getObject(item.objId);
                if (object.isPresent() && object1.isPresent()) {
                    return object.get().getType().equals(object1.get().getType());
                }
                return false;
            }
        });
    }

    public void updateUserInventory() {
        Item[] userItems = E(GameScreen.getPlayer()).getInventory().items;
        for (int i = 0; i < SIZE; i++) {
            Item item = i < userItems.length ? userItems[i] : null;
            slots.get(i).setItem(item);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = parentAlpha;
        if (!mouseOnInventory()) {
            alpha = 0.5f;
        }
        super.draw(batch, alpha);
        dragging.ifPresent(slot -> slot.getItem().ifPresent(item -> {
            Gdx.input.getY();
            Optional<Obj> object = ObjectHandler.getObject(item.objId);
            object.ifPresent(obj -> {
                TextureRegion graphic = ObjectHandler.getGraphic(obj);
                batch.draw(graphic, Gdx.input.getX() - (Slot.SIZE / 2), Gdx.graphics.getHeight() - Gdx.input.getY() - (Slot.SIZE / 2), getOriginX(), getOriginY(), graphic.getRegionWidth(), graphic.getRegionHeight(), Inventory.ZOOM, Inventory.ZOOM, 0);
            });
        }));
    }

    private boolean mouseOnInventory() {
        boolean xIN = Gdx.input.getX() > getX() && Gdx.input.getX() < getX() + getWidth();
        boolean yIN = Gdx.input.getY() > getY() && Gdx.input.getY() < getY() + getHeight() + getPadTop();
        return yIN && xIN;
    }
}
