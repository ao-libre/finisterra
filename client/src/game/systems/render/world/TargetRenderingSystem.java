package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import game.systems.render.BatchRenderingSystem;
import game.ui.GUI;
import game.utils.Colors;
import game.utils.WorldUtils;
import position.WorldPosOffsets;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Optional;

@Wire(injectInherited = true)
public class TargetRenderingSystem extends RenderingSystem {

    private static final Texture TARGET = new Texture(Gdx.files.local("data/ui/images/target.png"));
    private GUI gui;
    private BatchRenderingSystem batchRenderingSystem;

    public TargetRenderingSystem() {
        super(Aspect.all(Focused.class));
    }

    @Override
    protected void process(E e) {
        if (gui.getSpellView().toCast.isPresent()) {
            Optional<WorldPos> worldPos = WorldUtils.mouseToWorldPos();
            if (worldPos.isPresent()) {
                batchRenderingSystem.addTask(batch -> {
                    Color prevColor = new Color(batch.getColor());
                    batch.setColor(Colors.rgba(255, 255, 255, 0.5f));
                    WorldPosOffsets mousePos = Util.toScreen(worldPos.get());
                    batch.draw(TARGET, mousePos.x, mousePos.y, Tile.TILE_PIXEL_WIDTH, Tile.TILE_PIXEL_HEIGHT);
                    batch.setColor(prevColor);
                });

            }
        }
    }
}
