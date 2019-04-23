package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.handlers.ObjectHandler;
import shared.objects.types.Obj;

import java.util.Optional;

import static entity.character.info.Inventory.Item;

public class Slot extends Actor {

    static final int SIZE = 34;

    public static Texture selection = new Texture(Gdx.files.local("data/ui/images/slot-selection.png"));
    public static Texture background = new Texture(Gdx.files.local("data/ui/images/table-background.png"));
    private static Texture equip = new Texture(Gdx.files.local("data/ui/images/slot-equipped.png"));

    private Optional<Item> item = Optional.empty();

    private boolean selected;

    Slot() {
    }

    public Slot(Item item) {
        this.item = Optional.of(item);
    }

    private int getObjId() {
        return item.isPresent() ? item.get().objId : -1;
    }

    public int getCount() {
        return item.isPresent() ? item.get().count : 0;
    }

    void setItem(Item item) {
        this.item = Optional.ofNullable(item);
    }

    Optional<Item> getItem() {
        return item;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX(), getY(), SIZE, SIZE);
        if (item.isPresent()) {
            drawItem(batch);
            if (item.get().equipped) {
                batch.draw(equip, getX(), getY());
            }
        }
        if (selected) {
            batch.draw(selection, getX(), getY());
        }
    }

    private void drawItem(Batch batch) {
        Optional<Obj> object = ObjectHandler.getObject(getObjId());
        object.ifPresent(obj -> {
            TextureRegion graphic = ObjectHandler.getGraphic(obj);
            batch.draw(graphic, getX() + 1, getY() + 1);
        });
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

}
