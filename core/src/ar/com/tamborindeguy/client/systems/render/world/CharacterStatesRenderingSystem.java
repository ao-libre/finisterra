package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.character.Character;
import position.Pos2D;
import position.WorldPos;

import static ar.com.tamborindeguy.client.utils.Fonts.WRITING_FONT;
import static ar.com.tamborindeguy.client.utils.Fonts.dialogLayout;
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
    protected void process(int entity) {
        if (isInAnyState(entity)) {
            E player = E(entity);
            Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
            Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
            Pos2D screenPos = new Pos2D(cameraPos.x - playerPos.x, cameraPos.y - playerPos.y);

            if (player.hasWriting()) {
                cameraSystem.guiCamera.update();
                batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
                batch.begin();
                dialogLayout.setText(WRITING_FONT, ".");
                dialogLayout.setText(WRITING_FONT, ".", WRITING_FONT.getColor(), dialogLayout.width, Align.center, true);
                final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x + 8 - (dialogLayout.width / 2) - (Tile.TILE_PIXEL_WIDTH / 2) - 2;
                final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y + 40 + dialogLayout.height;
                WRITING_FONT.draw(batch, dialogLayout, fontX, fontY);
                batch.end();
            }
        }
    }

    private boolean isInAnyState(int entity) {
        return E(entity).hasMeditating() || E(entity).hasWriting() || E(entity).hasResting();
    }
}


