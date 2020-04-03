package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import entity.character.info.Bag;
import entity.character.info.Bag.Item;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.systems.ui.action_bar.systems.InventorySystem;
import game.utils.Skins;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class Inventory extends Window {

    private static final int COLUMNS = 5;
    private static final int ROWS = 4;
    private static final int SIZE = COLUMNS * ROWS;
    private final ClickListener mouseListener;
    public boolean toShoot = false;
    private int base;

    private ArrayList<Slot> slots;
    private AOAssetManager assetManager;
    private Optional<Slot> selected = Optional.empty();
    private Optional<Slot> dragging = Optional.empty();
    private Optional<Slot> origin = Optional.empty();
    private InventorySystem inventorySystem;

    public Inventory(InventorySystem inventorySystem) {
        super("", Skins.COMODORE_SKIN, "inventory");
        this.inventorySystem = inventorySystem;
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
        addListener(mouseListener = getMouseListener());
        this.assetManager = AOGame.getGlobalAssetManager();
    }

    public void selectItem(float x, float y, int tapCount) {
        selected.ifPresent(slot -> slot.setSelected(false));
        selected = getSlot(x, y);
        selected.ifPresent(slot -> {
            slot.setSelected(true);
            slot.getItem().ifPresent(item -> {
                if (tapCount >= 2) {
                    inventorySystem.use();
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
                        inventorySystem.swap(originIndex, targetIndex);

                    } else {
                        inventorySystem.dropItem(draggingIndex(), inventorySystem.getWorldPos((int) x, (int) y));
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
            slots.get(i).setItem(item, getGraphic(item));
        }
    }

    private TextureRegion getGraphic(Item item) {
        return Optional.ofNullable(item).map(i -> inventorySystem.getGraphic(i.objId)).orElse(null);
    }

//    public void getShoot() {
//        ObjectSystem objectSystem = WorldUtils.getWorld().orElse(null).getSystem(ObjectSystem.class);
//        Item[] items = E(GameScreen.getPlayer()).getInventory().items;
//
//        AtomicBoolean bowPresent = new AtomicBoolean(false);
//        AtomicBoolean arrowPresent = new AtomicBoolean(false);
//        for (int i = 0; i < items.length; i++) {
//            if (items[i] != null) {
//                int inventoryIndex = i;
//                objectSystem.getObject(items[i].objId).ifPresent(obj -> {
//                    if (items[inventoryIndex].equipped && obj.getType().equals(Type.WEAPON)) {
//                        WeaponObj weaponObj = (WeaponObj) obj;
//                        if (weaponObj.getKind().equals(WeaponKind.BOW)) {
//                            bowPresent.set(true);
//                        }
//                    }
//                    if (items[inventoryIndex].equipped && obj.getType().equals(Type.ARROW)) {
//                        arrowPresent.set(true);
//                    }
//                });
//            }
//        }
//        if (bowPresent.get() && arrowPresent.get()) {
//            WorldUtils.getWorld().ifPresent(world -> {
//                world.getSystem(UserInterfaceSystem.class).getConsole().addInfo(assetManager.getMessages(Messages.CLICK_TO_SHOOT));
//                Cursors.setCursor("arrow");
//                toShoot = true;
//            });
//        } else {
//            WorldUtils.getWorld().ifPresent(world -> {
//                world.getSystem(UserInterfaceSystem.class).getConsole().addInfo(assetManager.getMessages(Messages.DONT_HAVE_BOW_AND_ARROW));
//            });
//        }
//    }

    public void cleanShoot() {
        toShoot = false;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        dragging.flatMap(Slot::getItem).ifPresent(item -> {
            TextureRegion graphic = inventorySystem.getGraphic(item.objId);
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


    public boolean isOver() {
        return mouseListener.isOver();
    }

}
