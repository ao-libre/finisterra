package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import entity.character.parts.Body;
import entity.world.CombatMessage;
import game.handlers.DescriptorHandler;
import game.utils.Fonts;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

@Wire(injectInherited=true)
public class CombatRenderingSystem extends RenderingSystem {

    public static final float VELOCITY = 1f;

    public CombatRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(CombatMessage.class, Body.class, WorldPos.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());

        if (!player.hasCombatMessage()) {
            // TODO bug here, sometimes getter return null
            return;
        }
        CombatMessage combatMessage = player.getCombatMessage();
        combatMessage.offset = Interpolation.pow2OutInverse.apply(combatMessage.time / CombatMessage.DEFAULT_TIME) * CombatMessage.DEFAULT_OFFSET;
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
                case ENERGY:
                    font = Fonts.ENERGY_FONT;
                    break;
                default:
                    font = Fonts.COMBAT_FONT;
            }

            Color copy = font.getColor().cpy();
            if (combatMessage.time < CombatMessage.START_ALPHA) {
                combatMessage.alpha = MathUtils.clamp(combatMessage.time / CombatMessage.START_ALPHA, 0f, 1f);
                font.getColor().a = combatMessage.alpha;
                font.getColor().premultiplyAlpha();
            }

            Fonts.dialogLayout.setText(font, combatMessage.text);
            float width = Fonts.dialogLayout.width;
            Fonts.dialogLayout.setText(font, combatMessage.text, font.getColor(), width, Align.center, true);
            final float fontX = playerPos.x + (Tile.TILE_PIXEL_WIDTH - Fonts.dialogLayout.width) / 2;
            int bodyOffset = DescriptorHandler.getBody(player.getBody().index).getHeadOffsetY();
            final float fontY = playerPos.y + combatMessage.offset + bodyOffset - 70
                    + Fonts.dialogLayout.height;
            font.draw(getBatch(), Fonts.dialogLayout, fontX, fontY);
            font.setColor(copy);
        } else {
            player.removeCombatMessage();
        }
    }

}
