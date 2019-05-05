package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.character.parts.Body;
import entity.world.Dialog;
import game.handlers.DescriptorHandler;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.utils.Fonts;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;

@Wire
public class DialogRenderingSystem extends OrderedEntityProcessingSystem {

    private static final int ALPHA_TIME = 2;
    private static final int MAX_LENGTH = 120;
    private static final int DISTANCE_TO_TOP = 5;
    private static final float TIME = 0.3f;
    private static final float VELOCITY = DISTANCE_TO_TOP / TIME;
    private SpriteBatch batch;
    private CameraSystem cameraSystem;
    private CharacterRenderingSystem characterRenderingSystem;

    public DialogRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Dialog.class, Body.class, WorldPos.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        E player = E.E(e);
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
        Dialog dialog = player.getDialog();
        dialog.time -= world.getDelta();
        if (dialog.time > 0) {
            BitmapFont font = dialog.kind.equals(Dialog.Kind.MAGIC_WORDS) ? Fonts.MAGIC_FONT : Fonts.DIALOG_FONT;
            Color copy = font.getColor().cpy();
            if (dialog.time < ALPHA_TIME) {
                dialog.alpha = dialog.time / ALPHA_TIME;
                font.getColor().a = dialog.alpha;
            }

            Fonts.dialogLayout.setText(font, dialog.text);
            int lines = Math.max(1, (int) Fonts.dialogLayout.width / MAX_LENGTH);
            float width = Math.min(Fonts.dialogLayout.width, MAX_LENGTH);
            Fonts.dialogLayout.setText(font, dialog.text, font.getColor(), width, Align.center | Align.top, true);
            final float fontX = playerPos.x + (Tile.TILE_PIXEL_WIDTH - width) / 2;
            float up = Dialog.DEFAULT_TIME - dialog.time <= TIME ? (Dialog.DEFAULT_TIME - dialog.time) * VELOCITY : DISTANCE_TO_TOP;
            float offsetY = DescriptorHandler.getBody(player.getBody().index).getHeadOffsetY();
            final float fontY = playerPos.y - 65 + offsetY - up + Fonts.dialogLayout.height;
            font.draw(batch, Fonts.dialogLayout, fontX, fontY);
            font.setColor(copy);
        } else {
            player.removeDialog();
        }
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E.E(entity).getWorldPos().y);
    }
}
