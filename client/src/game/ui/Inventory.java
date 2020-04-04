package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import component.entity.character.info.Bag;
import component.entity.character.info.Bag.Item;
import game.utils.Skins;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Inventory extends Window {

    private static final int COLUMNS = 5;
    private static final int ROWS = 4;
    private static final int SIZE = COLUMNS * ROWS;
    private int base;

    private ArrayList<Slot> slots;
    private Optional<Slot> selected = Optional.empty();
    private Optional<Slot> dragging = Optional.empty();
    private Optional<Slot> origin = Optional.empty();

    public Inventory() {
        super("", Skins.COMODORE_SKIN, "inventory");
        int columnsCounter = 1;
        setMovable(false);
        this.slots = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            Slot newSlot = new Slot();
            slots.add(newSlot);
            add(slots.get(i)).width(Slot.SIZE).height(Slot.SIZE);
            if (columnsCounter > ROWS - 1) {
                row();
                columnsCounter = 0;
            }
            columnsCounter++;
        }
        addListener(getMouseListener());
    }

    public void selectItem(float x, float y, int tapCount) {
        selected.ifPresent(slot -> slot.setSelected(false));
        selected = getSlot(x, y);
        selected.ifPresent(slot -> {
            slot.setSelected(true);
            slot.getItem().ifPresent(item -> {
                if (tapCount >= 2) {
                    doubleClick();
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
                .map(Slot.class::cast)
                .findFirst();
    }

    private ClickListener getMouseListener() {
        return new ClickListener() {

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                Inventory.this.scrolled(amount);
                return true;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                int tapCount = getTapCount();
                selectItem(x, y, tapCount);
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
                    Optional<Slot> slot = getSlot(x, y);
                    if (slot.isPresent()) {
                        Slot target = slot.get();
                        int targetIndex = base + slots.indexOf(target);
                        int originIndex = base + slots.indexOf(dragging.get());
                        swap(originIndex, targetIndex);
                    } else {
                        dragAndDropOut(draggingIndex(), (int) x, (int) y);
                    }
                }
                dragging = Optional.empty();
            }
        };
    }

    private void scrolled(int amount) {
        base += amount;
        base = MathUtils.clamp(base, 0, Bag.SIZE - Inventory.SIZE);
    }

    public void update(Bag userBag) {
        update(base, userBag);
    }

    public void update(int base, Bag userBag) {
        Item[] userItems = userBag.items;
        for (int i = 0; i < SIZE; i++) {
            Item item = base + i < userItems.length ? userItems[base + i] : null;
            slots.get(i).setItem(item, item != null ? getGraphic(item) : null);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        dragging.ifPresent(slot -> {
            TextureRegion graphic = slot.getGraphic();
            if (graphic != null) {
                int x1 = Gdx.input.getX() - (graphic.getRegionWidth() / 2);
                int y1 = Gdx.graphics.getHeight() - Gdx.input.getY() - (graphic.getRegionHeight() / 2);
                batch.draw(graphic, x1, y1);
            }
        });
    }

    public Optional<Slot> getSelected() {
        return selected;
    }

    public int selectedIndex() {
        assert (selected.isPresent());
        return base + slots.indexOf(selected.get());
    }

    private int draggingIndex() {
        assert (dragging.isPresent());
        return base + slots.indexOf(dragging.get());
    }

    protected abstract void doubleClick();

    protected abstract void dragAndDropOut(int i, int x, int y);

    protected abstract void swap(int originIndex, int targetIndex);

    protected abstract TextureRegion getGraphic(Item item);


}
