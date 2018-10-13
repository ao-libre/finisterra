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
import static java.util.Collections.swap;

public class Inventory extends Window {

    private static final int SIZE = 20;
    public static final int COLUMNS = 5;
    public static final float ZOOM = 1.35f;

    private ArrayList<Slot> slots;
    private Optional<Slot> selected = Optional.empty();
    private Optional<Slot> dragging = Optional.empty();
    private Optional<Slot> origin = Optional.empty();
    private boolean canDrag;

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
                return Stream.of(getChildren().items).filter(actor -> {
                    if (x > actor.getX() && x < actor.getWidth() + actor.getX()) {
                        if (y > actor.getY() && y < actor.getHeight() + actor.getY()) {
                            return true;
                        }
                    }
                    return false;
                }).map(Slot.class::cast).findFirst();
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
                    ArrayList<Item> userItems = E(GameScreen.getPlayer()).getInventory().items;
                    // notify server
                    getSlot(x, y).ifPresent(target -> {
                        swap(userItems, slots.indexOf(dragging.get()), slots.indexOf(target));
                        updateUserInventory(GameScreen.getPlayer());
                    });

                }
                dragging = Optional.empty();
            }

            private void unequip(ArrayList<Slot> slots) {
                slots.stream().filter(slot -> !slot.equals(selected.get())).forEach(slot -> {
                    slot.getItem().ifPresent(item -> {
                        Item equippedItem = selected.get().getItem().get();
                        if (sameKind(equippedItem, item)) {
                            item.equipped = false;
                            InventorySystem.equip(item.objId, item.equipped);
                        }
                    });
                });
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

    public void updateUserInventory(int player) {
        ArrayList<Item> userItems = E(player).getInventory().items;
        for (int i = 0; i < SIZE; i++) {
            Item item = i < userItems.size() ? userItems.get(i) : null;
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
        if (dragging.isPresent()) {
            dragging.get().getItem().ifPresent(item -> {
                Gdx.input.getY();
                Optional<Obj> object = ObjectHandler.getObject(item.objId);
                object.ifPresent(obj -> {
                    TextureRegion graphic = ObjectHandler.getGraphic(obj);
                    batch.draw(graphic, Gdx.input.getX() - (Slot.SIZE / 2), Gdx.graphics.getHeight() - Gdx.input.getY() - (Slot.SIZE / 2), getOriginX(), getOriginY(), graphic.getRegionWidth(), graphic.getRegionHeight(), Inventory.ZOOM, Inventory.ZOOM, 0);
                });
            });
        }
    }

    private boolean mouseOnInventory() {
        boolean xIN = Gdx.input.getX() > getX() && Gdx.input.getX() < getX() + getWidth();
        boolean yIN = Gdx.input.getY() > getY() && Gdx.input.getY() < getY() + getHeight();
        return yIN && xIN;
    }
}
