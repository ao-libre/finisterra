package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.objects.types.Obj;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Optional;

public class Slot extends Actor {

    public static final int SIZE = 34;

    private static Texture selection = new Texture(Gdx.files.local("data/ui/images/slot-selection.png"));
    private static Texture background = new Texture(Gdx.files.local("data/ui/images/slot-background.png"));
    private static Texture equip = new Texture(Gdx.files.local("data/ui/images/slot-equipped.png"));

    private int objId = -1;
    private int count;

    private boolean selected;
    private boolean equipped;

    public Slot() {
        this(-1, 0);
    }

    public Slot(int objId) {
        this(objId, 1);
    }

    public Slot(int objId, int count) {
        this.objId = objId;
        this.count = count;
    }

    public int getObjId() {
        return objId;
    }

    public void setObjId(int objId) {
        this.objId = objId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawTexture(batch, background);
        Optional<Obj> object = ObjectHandler.getObject(objId);
        object.ifPresent(obj -> {
            TextureRegion graphic = ObjectHandler.getGraphic(obj);
            drawScaled(batch, graphic, 1, 1);
//            batch.draw(graphic, getX() + 1, getY() + 1);
        });
        if (selected) {
            drawTexture(batch, selection);
        }
        if (equipped) {
            drawTexture(batch, equip);
        }
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

    public void toggleEquipped() {
        this.equipped = !equipped;
    }

    public boolean isEquipped() {
        return equipped;
    }
}
