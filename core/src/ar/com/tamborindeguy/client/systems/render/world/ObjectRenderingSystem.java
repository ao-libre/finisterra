package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.handlers.DescriptorsHandler;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.textures.GameTexture;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import entity.Object;
import position.Pos2D;
import position.WorldPos;

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
    protected void process(int objectId) {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        WorldPos objectPos = E(objectId).getWorldPos();
        Pos2D screenPos = Util.toScreen(objectPos);
        Obj object = DescriptorsHandler.getObject(E(objectId).getObject().index);
        // TODO fix this. Dont create texture every time.
        GameTexture gameTexture = new GameTexture(object.getGrhIndex());
        TextureRegion region = gameTexture.getGraphic();
        if (region != null) {
            batch.draw(region, screenPos.x, screenPos.y);
        }
        gameTexture.dispose();
        batch.end();
    }
}
