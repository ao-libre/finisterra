package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import entity.character.info.Inventory.Item;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.handlers.ObjectHandler;
import game.screens.GameScreen;
import game.utils.Cursors;
import game.utils.Skins;
import game.utils.WorldUtils;
import shared.network.interaction.DropItem;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.objects.types.*;
import shared.util.Messages;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.artemis.E.E;

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

    Inventory() {
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
        this.mouseListener = getMouseListener();
        addListener(mouseListener);
        this.assetManager = AOGame.getGlobalAssetManager();
    }

    private ClickListener getMouseListener() {
        return new ClickListener() {

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                Inventory.this.scrolled(amount);
                return false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected.ifPresent(slot -> slot.setSelected(false));
                selected = getSlot(x, y);
                selected.ifPresent(slot -> {
                    slot.setSelected(true);
                    slot.getItem().ifPresent(item -> {
                        if (getTapCount() >= 2) {
                            GameScreen.getClient().sendToAll(new ItemActionRequest(slots.indexOf(slot)));
                            addSpellSVE(slot);
                            isBowORArrow(slot);
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
                    Optional<Slot> slot = getSlot(x, y);
                    if (slot.isPresent()) {
                        Slot target = slot.get();
                        InventoryUpdate update = new InventoryUpdate(E(GameScreen.getPlayer()).getNetwork().id);
                        int targetIndex = base + slots.indexOf(target);
                        int originIndex = base + slots.indexOf(dragging.get());
                        Item originItem = userItems[originIndex];
                        if (userItems[targetIndex] != null) {
                            update.add(targetIndex, originItem);
                            update.add(originIndex, userItems[targetIndex]);
                            swap(userItems, originIndex, targetIndex);
                        } else {
                            update.add(targetIndex, originItem);
                            update.remove(originIndex);
                            userItems[targetIndex] = originItem;
                            userItems[originIndex] = null;
                        }
                        GameScreen.getClient().sendToAll(update);
                        updateUserInventory(base);
                    } else {
                        WorldUtils.mouseToWorldPos().ifPresent(worldPos -> GameScreen.getClient().sendToAll(new DropItem(E(GameScreen.getPlayer()).getNetwork().id, draggingIndex(), worldPos)));
                    }
                }
                dragging = Optional.empty();
            }

            final <T> void swap(T[] a, int i, int j) {
                T t = a[i];
                a[i] = a[j];
                a[j] = t;
            }

        };
    }

    void scrolled(int amount) {
        base += amount;
        base = MathUtils.clamp(base, 0, entity.character.info.Inventory.SIZE - Inventory.SIZE);
        updateUserInventory(base);
    }

    public void updateUserInventory(int base) {
        if (base < 0) {
            base = this.base;
        }
        Item[] userItems = E(GameScreen.getPlayer()).getInventory().items;
        for (int i = 0; i < SIZE; i++) {
            Item item = base + i < userItems.length ? userItems[base + i] : null;
            slots.get(i).setItem(item);
        }
    }

    public void getShoot() {
        ObjectHandler objectHandler = WorldUtils.getWorld().orElse(null).getSystem(ObjectHandler.class);
        Item[] items = E(GameScreen.getPlayer()).getInventory().items;

        AtomicBoolean bowPresent = new AtomicBoolean(false);
        AtomicBoolean arrowPresent = new AtomicBoolean(false);
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                int inventoryIndex = i;
                objectHandler.getObject(items[i].objId).ifPresent(obj -> {
                    if (items[inventoryIndex].equipped && obj.getType().equals(Type.WEAPON)) {
                        WeaponObj weaponObj = (WeaponObj) obj;
                        if (weaponObj.getKind().equals(WeaponKind.BOW)) {
                            bowPresent.set(true);
                        }
                    }
                    if (items[inventoryIndex].equipped && obj.getType().equals(Type.ARROW)) {
                        arrowPresent.set(true);
                    }
                });
            }
        }
        if (bowPresent.get() && arrowPresent.get()) {
            WorldUtils.getWorld().ifPresent(world -> {
                world.getSystem(GUI.class).getConsole().addInfo(assetManager.getMessages(Messages.CLICK_TO_SHOOT));
                Cursors.setCursor("arrow");
                toShoot = true;
            });
        } else {
            WorldUtils.getWorld().ifPresent(world -> {
                world.getSystem(GUI.class).getConsole().addInfo(assetManager.getMessages(Messages.DONT_HAVE_BOW_AND_ARROW));
            });
        }
    }

    public void cleanShoot() {
        toShoot = false;
    }

    //chequea si es arco o flecha
    public void isBowORArrow(Slot slot) {
        Optional<Obj> object = slotToObject(slot);
        object.ifPresent(obj -> {
            if (obj.getType().equals(Type.WEAPON) || obj.getType().equals(Type.ARROW)) {
                cleanShoot();
                Cursors.setCursor("hand");
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        dragging.flatMap(Slot::getItem).ifPresent(item -> {
            ObjectHandler objectHandler = WorldUtils.getWorld().orElse(null).getSystem(ObjectHandler.class);
            Optional<Obj> object = objectHandler.getObject(item.objId);
            object.ifPresent(obj -> {
                TextureRegion graphic = objectHandler.getGraphic(obj);
                int x1 = Gdx.input.getX() - (graphic.getRegionWidth() / 2);
                int y1 = Gdx.graphics.getHeight() - Gdx.input.getY() - (graphic.getRegionHeight() / 2);
                batch.draw(graphic, x1, y1);
            });
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

    //recupera el objeto de un slot
    private Optional<Obj> slotToObject(Slot slot) {
        Optional<Item> item = slot.getItem();
        int objID = item.map(item1 -> item1.objId).orElse(-1);
        ObjectHandler objectHandler = WorldUtils.getWorld().orElse(null).getSystem(ObjectHandler.class);
        Optional<Obj> object = objectHandler.getObject(objID);
        return object;
    }


    private void addSpellSVE(Slot slot) { //funcion provisoria hasta q encuentre como hacerlo a desde el servidor
        Optional<Obj> object = slotToObject(slot);
        object.ifPresent(obj -> {
            if (obj.getType().equals(Type.SPELL)) {
                SpellObj spellObj = (SpellObj) obj;
                if (E(GameScreen.getPlayer()).charHeroHeroId() != 0) {
                    GameScreen.world.getSystem(GUI.class).getSpellViewExpanded().newSpellAdd(spellObj.getSpellIndex());
                }
            }
        });
    }

    public boolean isOver() {
        return mouseListener.isOver();
    }

}
