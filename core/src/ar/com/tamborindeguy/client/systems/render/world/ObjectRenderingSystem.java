package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.client.ui.GUI;
import ar.com.tamborindeguy.client.ui.Slot;
import ar.com.tamborindeguy.client.utils.Colors;
import ar.com.tamborindeguy.client.utils.WorldUtils;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.Object;
import position.Pos2D;
import position.WorldPos;

import java.util.Optional;

import static com.artemis.E.E;

@Wire
public class ObjectRenderingSystem extends IteratingSystem {

    private SpriteBatch batch;
    private CameraSystem cameraSystem;

    public ObjectRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Object.class, WorldPos.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void process(int objectId) {
        Optional<Obj> object = ObjectHandler.getObject(E(objectId).getObject().index);
        object.ifPresent(obj -> {
            WorldPos objectPos = E(objectId).getWorldPos();
            Pos2D screenPos = Util.toScreen(objectPos);
            batch.draw(ObjectHandler.getIngameGraphic(obj), screenPos.x - Tile.TILE_PIXEL_WIDTH, screenPos.y - Tile.TILE_PIXEL_HEIGHT);
        });
    }

    @Override
    protected void end() {
        batch.end();
    }

}
