package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.world.Object;
import game.handlers.ObjectHandler;
import position.Pos2D;
import position.WorldPos;
import shared.objects.types.Obj;
import shared.util.Util;

import java.util.Optional;

import static com.artemis.E.E;

@Wire(injectInherited=true)
public class ObjectRenderingSystem extends RenderingSystem {

    public ObjectRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Object.class, WorldPos.class), batch, RenderingSystem.CameraKind.WORLD);
    }

    @Override
    protected void process(E e) {
        Optional<Obj> object = ObjectHandler.getObject(e.getObject().index);
        object.ifPresent(obj -> {
            WorldPos objectPos = e.getWorldPos();
            Pos2D screenPos = Util.toScreen(objectPos);
            getBatch().draw(ObjectHandler.getIngameGraphic(obj), screenPos.x, screenPos.y);
        });
    }

}
