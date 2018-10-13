package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.objects.types.Obj;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Optional;

import static entity.character.info.Inventory.*;

public class Slot extends Actor {

    public static final int SIZE = 34;

    private static Texture selection = new Texture(Gdx.files.local("data/ui/images/slot-selection.png"));
    private static Texture background = new Texture(Gdx.files.local("data/ui/images/slot-background.png"));
    private static Texture equip = new Texture(Gdx.files.local("data/ui/images/slot-equipped.png"));

    private Optional<Item> item = Optional.empty();

    private boolean selected;
    private boolean equipped;

    public Slot() {}

    public Slot(Item item) {
        this.item = Optional.of(item);
    }

    public int getObjId() {
        return item.isPresent() ? item.get().objId : -1;
    }

    public int getCount() {
        return item.isPresent() ? item.get().count : 0;
    }

    public void setItem(Item item){
        this.item = Optional.ofNullable(item);
    }

    public Optional<Item> getItem() {
        return item;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawTexture(batch, background);
        if (item.isPresent()) {
            drawItem(batch);
            if (item.get().equipped) {
                drawTexture(batch, equip);
            }
        }
        if (selected) {
            drawTexture(batch, selection);
        }
    }

    private void drawItem(Batch batch) {
        Optional<Obj> object = ObjectHandler.getObject(getObjId());
        object.ifPresent(obj -> {
            TextureRegion graphic = ObjectHandler.getGraphic(obj);
            drawScaled(batch, graphic, 1, 1);
        });
    }

    private void drawTexture(Batch batch, Texture texture) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), 1, 1, getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    private void drawScaled(Batch batch, TextureRegion texture, float offsetX, float offsetY) {
        batch.draw(texture, getX() + offsetX, getY() + offsetY, getOriginX(), getOriginY(), texture.getRegionWidth(), texture.getRegionHeight(), Inventory.ZOOM,  Inventory.ZOOM, 0) ;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
