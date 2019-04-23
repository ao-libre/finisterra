package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.world.Object;
import game.handlers.ObjectHandler;
import game.systems.camera.CameraSystem;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.objects.types.Obj;
import shared.util.Util;

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
            batch.draw(ObjectHandler.getIngameGraphic(obj), screenPos.x, screenPos.y - Tile.TILE_PIXEL_HEIGHT);
        });
    }

    @Override
    protected void end() {
        batch.end();
    }

}
