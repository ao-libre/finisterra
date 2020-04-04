package game.systems.render.world;

import component.camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import game.systems.render.BatchRenderingSystem;
import game.systems.ui.UserInterfaceSystem;
import game.utils.Colors;
import component.position.WorldPos;
import component.position.WorldPosOffsets;
import shared.model.map.Tile;
import shared.util.WorldPosConversion;

@Wire(injectInherited = true)
public class TargetRenderingSystem extends RenderingSystem {

    private static final Texture TARGET = new Texture(Gdx.files.local("data/ui/images/target.png"));
    private UserInterfaceSystem userInterfaceSystem;
    private BatchRenderingSystem batchRenderingSystem;

    public TargetRenderingSystem() {
        super(Aspect.all(Focused.class));
    }

    @Override
    protected void process(E e) {
        WorldPos worldPos = userInterfaceSystem.getWorldPos(Gdx.input.getX(), Gdx.input.getY());
        batchRenderingSystem.addTask(batch -> {
            Color prevColor = new Color(batch.getColor());
            batch.setColor(Colors.rgba(255, 255, 255, 0.5f));
            WorldPosOffsets mousePos = WorldPosConversion.toScreen(worldPos);
            batch.draw(TARGET, mousePos.x, mousePos.y, Tile.TILE_PIXEL_WIDTH, Tile.TILE_PIXEL_HEIGHT);
            batch.setColor(prevColor);
        });
    }
}
