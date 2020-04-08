package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import component.entity.world.Object;
import game.systems.resources.ObjectSystem;
import game.systems.render.BatchRenderingSystem;
import component.position.WorldPos;
import component.position.WorldPosOffsets;
import shared.model.map.Tile;
import shared.objects.types.Obj;
import shared.util.WorldPosConversion;

import java.util.Optional;

@Wire(injectInherited = true)
public class ObjectRenderingSystem extends RenderingSystem {

    private ObjectSystem objectSystem;
    private BatchRenderingSystem batchRenderingSystem;

    public ObjectRenderingSystem() {
        super(Aspect.all(Object.class, WorldPos.class));
    }

    @Override
    protected void process(E e) {
        Optional<Obj> object = objectSystem.getObject(e.getObject().index);
        object.ifPresent(obj -> {
            WorldPos objectPos = e.getWorldPos();
            WorldPosOffsets screenPos = WorldPosConversion.toScreen(objectPos);
            if (!e.hasScale()) {
                e.scale(0f);
            } else if (e.getScale().scale >= 1.0f) {
                e.getScale().scale = 1f;
            } else {
                e.getScale().scale += world.delta * 2;
            }
            float scale = Interpolation.swingOut.apply(e.getScale().scale);
            TextureRegion texture = objectSystem.getIngameGraphic(obj);
            float width = scale * texture.getRegionWidth();
            float height = scale * texture.getRegionHeight();
            float x = screenPos.x + (Tile.TILE_PIXEL_WIDTH - width) / 2;
            float y = screenPos.y + (Tile.TILE_PIXEL_HEIGHT - height) / 2;
            batchRenderingSystem.addTask(batch -> batch.draw(texture, x, y, width, height));
        });
    }
}
