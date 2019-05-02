package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.character.parts.Body;
import entity.world.CombatMessage;
import game.handlers.DescriptorHandler;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.utils.Fonts;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;

import static com.artemis.E.E;

public class CombatRenderingSystem extends OrderedEntityProcessingSystem {

    public static final float VELOCITY = 25.0f;
    private static final float FONT_DARKNESS_PERCENT = 10f;
    private SpriteBatch batch;
    private CameraSystem cameraSystem;

    public CombatRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(CombatMessage.class, Body.class, WorldPos.class));
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
    protected void process(Entity e) {
        E player = E(e);
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
        Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
        Pos2D screenPos = new Pos2D(cameraPos.x - playerPos.x, cameraPos.y - playerPos.y);

        if (!player.hasCombatMessage()) {
            // TODO bug here, sometimes getter return null
            return;
        }
        CombatMessage combatMessage = player.getCombatMessage();
        combatMessage.offset -= getWorld().getDelta() * combatMessage.time * VELOCITY;
        if (combatMessage.offset < 0) {
            combatMessage.offset = 0;
        }

        combatMessage.time -= world.getDelta();
        if (combatMessage.time > 0) {
            BitmapFont font;
            switch (combatMessage.kind) {
                case PHYSICAL:
                    font = Fonts.COMBAT_FONT;
                    break;
                case STAB:
                    font = Fonts.STAB_FONT;
                    break;
                case MAGIC:
                    font = combatMessage.text.startsWith("+") ? Fonts.GM_NAME_FONT : Fonts.MAGIC_COMBAT_FONT;
                    break;
                default:
                    font = Fonts.COMBAT_FONT;
            }

            Color copy = font.getColor().cpy();
            if (combatMessage.time < CombatMessage.DEFAULT_ALPHA) {
                combatMessage.alpha = combatMessage.time / CombatMessage.DEFAULT_ALPHA;
                font.getColor().a = combatMessage.alpha;
                font.getColor().premultiplyAlpha();
            }

            Fonts.dialogLayout.setText(font, combatMessage.text);
            float width = Fonts.dialogLayout.width;
            Fonts.dialogLayout.setText(font, combatMessage.text, font.getColor(), width, Align.center, true);
            final float fontX =
                (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x + (Tile.TILE_PIXEL_WIDTH - Fonts.dialogLayout.width) / 2;
            int bodyOffset = 40 - DescriptorHandler.getBody(player.getBody().index).getHeadOffsetY();
            final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y - combatMessage.offset + bodyOffset
                + Fonts.dialogLayout.height; //40 should be the Y offset of the entity
            font.draw(batch, Fonts.dialogLayout, fontX, fontY);
            font.setColor(copy);
        } else {
            player.removeCombatMessage();
        }
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
