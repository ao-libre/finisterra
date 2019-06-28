package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.handlers.ObjectHandler;
import game.screens.GameScreen;
import game.utils.Skins;
import shared.objects.types.Obj;

import java.util.Optional;

import static entity.character.info.Inventory.Item;

public class Slot extends ImageButton {

    static final int SIZE = 64;

    private static Drawable selection = Skins.COMODORE_SKIN.getDrawable("slot-selected");
    private static Texture equip = new Texture(Gdx.files.local("data/ui/images/slot-equipped.png"));

    private Optional<Item> item = Optional.empty();

    private boolean selected;

    Slot() {
        super(Skins.COMODORE_SKIN, "icon-container");
    }

    public Slot(Item item) {
        this();
        this.item = Optional.of(item);
    }

    private int getObjId() {
        return item.map(item1 -> item1.objId).orElse(-1);
    }

    public int getCount() {
        return item.map(item1 -> item1.count).orElse(0);
    }

    Optional<Item> getItem() {
        return item;
    }

    void setItem(Item item) {
        this.item = Optional.ofNullable(item);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch,parentAlpha);
        if (item.isPresent()) {
            drawItem(batch);
            if (item.get().equipped) {
                batch.draw(equip, getX(), getY(), SIZE, SIZE);
            }
            if (selected) {
                selection.draw(batch, getX(), getY(), SIZE, SIZE);
            }
        }
    }

    private void drawItem(Batch batch) {
        ObjectHandler objectHandler = GameScreen.getWorld().getSystem(ObjectHandler.class);
        Optional<Obj> object = objectHandler.getObject(getObjId());
        object.ifPresent(obj -> {
            TextureRegion graphic = objectHandler.getGraphic(obj);
            batch.draw(graphic, getX() + 1, getY() + 1);
        });
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

}
