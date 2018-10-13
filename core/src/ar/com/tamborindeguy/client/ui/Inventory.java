package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.utils.Skins;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.inventory.ItemActionRequest;
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

    static final int COLUMNS = 5;
    static final int ROWS = 4;
    static final float ZOOM = 1.35f;
    private static final int SIZE = COLUMNS * ROWS;

    private ArrayList<Slot> slots;
    private Optional<Slot> selected = Optional.empty();
    private Optional<Slot> dragging = Optional.empty();
    private Optional<Slot> origin = Optional.empty();

    Inventory() {
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
                            GameScreen.getClient().sendToAll(new ItemActionRequest(slots.indexOf(slot)));
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
                        InventoryUpdate update = new InventoryUpdate(E(GameScreen.getPlayer()).getNetwork().id);
                        int targetIndex = slots.indexOf(target);
                        int originIndex = slots.indexOf(dragging.get());
                        Item originItem = userItems[originIndex];
                        if (userItems[targetIndex] != null) {
                            update.add(targetIndex, originItem);
                            update.add(originIndex, userItems[targetIndex]);
                            swap(userItems, originIndex, targetIndex);
                        } else {
                            update.add(targetIndex, originItem);
                            userItems[targetIndex] = originItem;
                        }
                        GameScreen.getClient().sendToAll(update);
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
