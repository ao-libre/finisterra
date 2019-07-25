package game.systems.render.world;

import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.ui.GUI;
import game.utils.Colors;
import game.utils.WorldUtils;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Optional;

@Wire(injectInherited = true)
public class TargetRenderingSystem extends RenderingSystem {

    private GUI gui;
    private static Texture target = new Texture(Gdx.files.local("data/ui/images/target.png"));

    public TargetRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Focused.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E e) {
        if (gui.getSpellView().toCast.isPresent()) {
            Optional<WorldPos> worldPos = WorldUtils.mouseToWorldPos();
            if (worldPos.isPresent()) {
                Color prevColor = new Color(getBatch().getColor());
                getBatch().setColor(Colors.rgba(255, 255, 255, 0.5f));
                Pos2D mousePos = Util.toScreen(worldPos.get());
                getBatch().draw(target, mousePos.x, mousePos.y, Tile.TILE_PIXEL_WIDTH, Tile.TILE_PIXEL_HEIGHT);
                getBatch().setColor(prevColor);
            }
        }
    }
}
