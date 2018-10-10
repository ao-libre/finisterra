package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.objects.types.Obj;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Optional;

public class Slot extends Actor {

    public static final int SIZE = 32;

    private int objId = -1;
    private int count;

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
        Optional<Obj> object = ObjectHandler.getObject(objId);
        object.ifPresent(obj -> {
            TextureRegion graphic = ObjectHandler.getGraphic(obj);
//            batch.draw(graphic, getX(), getY(), getOriginX(), getOriginY(), graphic.getRegionWidth() * AO.GAME_SCREEN_ZOOM, graphic.getRegionHeight() * AO.GAME_SCREEN_ZOOM, AO.GAME_SCREEN_ZOOM, AO.GAME_SCREEN_ZOOM, 0);
            batch.draw(graphic, getX(), getY(), graphic.getRegionWidth() * AO.GAME_SCREEN_ZOOM, graphic.getRegionHeight() * AO.GAME_SCREEN_ZOOM);
        });
    }

}
