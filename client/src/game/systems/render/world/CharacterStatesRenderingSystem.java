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

@Wire
public class CharacterStatesRenderingSystem extends IteratingSystem {

    private final SpriteBatch batch;

    private CameraSystem cameraSystem;

    public CharacterStatesRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(int entity) {
        if (isInAnyState(entity)) {
            E player = E(entity);
            Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
            Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
            Pos2D screenPos = new Pos2D(cameraPos.x - playerPos.x, cameraPos.y - playerPos.y);

            if (player.hasWriting()) {
                Fonts.dialogLayout.setText(Fonts.WRITING_FONT, ".");
                Fonts.dialogLayout.setText(Fonts.WRITING_FONT, ".", Fonts.WRITING_FONT.getColor(), Fonts.dialogLayout.width, Align.center, true);
                final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x + 8 - (Fonts.dialogLayout.width / 2) - (Tile.TILE_PIXEL_WIDTH / 2) - 2;
                final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y + 40 + Fonts.dialogLayout.height;
                Fonts.WRITING_FONT.draw(batch, Fonts.dialogLayout, fontX, fontY);
            }
        }
        if (GUI.getSpellView().toCast.isPresent()) {
            Optional<WorldPos> worldPos = WorldUtils.mouseToWorldPos();
            if (worldPos.isPresent()) {
                batch.setColor(Colors.TRANSPARENT_RED);
                Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
                Pos2D mousePos = Util.toScreen(worldPos.get());
                Pos2D screenPos = new Pos2D(cameraPos.x - mousePos.x, cameraPos.y - mousePos.y);
                batch.draw(Slot.selection, (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x, (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y, Tile.TILE_PIXEL_WIDTH, Tile.TILE_PIXEL_HEIGHT);
                batch.setColor(Color.WHITE);
            }
        }
    }

    private boolean isInAnyState(int entity) {
        return E(entity).hasMeditating() || E(entity).hasWriting() || E(entity).hasResting();
    }
}


