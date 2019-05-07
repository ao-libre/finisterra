package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.character.Character;
import game.systems.camera.CameraSystem;
import game.ui.GUI;
import game.ui.Slot;
import game.utils.Colors;
import game.utils.Fonts;
import game.utils.WorldUtils;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Optional;

import static com.artemis.E.E;

@Wire(injectInherited=true)
public class CharacterStatesRenderingSystem extends RenderingSystem {

    public CharacterStatesRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E player) {
        if (isInAnyState(player)) {
            Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
            if (player.hasWriting()) {
                Fonts.dialogLayout.setText(Fonts.WRITING_FONT, "...");
                Fonts.dialogLayout.setText(Fonts.WRITING_FONT, "...", Fonts.WRITING_FONT.getColor(), Fonts.dialogLayout.width, Align.center, true);
                final float fontX = playerPos.x + 8 - (Fonts.dialogLayout.width / 2) - (Tile.TILE_PIXEL_WIDTH / 2) - 2;
                final float fontY = playerPos.y - 40 + Fonts.dialogLayout.height;
                Fonts.WRITING_FONT.draw(getBatch(), Fonts.dialogLayout, fontX, fontY);
            }
        }
    }

    private boolean isInAnyState(E entity) {
        return entity.hasMeditating() || entity.hasWriting() || entity.hasResting();
    }
}


