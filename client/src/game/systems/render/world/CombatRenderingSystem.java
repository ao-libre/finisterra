package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import entity.character.parts.Body;
import entity.world.CombatMessage;
import game.handlers.DescriptorHandler;
import game.utils.Colors;
import game.utils.Skins;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.concurrent.TimeUnit;

@Wire(injectInherited = true)
public class CombatRenderingSystem extends RenderingSystem {

    public static final float VELOCITY = 1f;
    private DescriptorHandler descriptorHandler;
    private LoadingCache<CombatMessage, Table> messages = CacheBuilder
            .newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Table load(CombatMessage message) {
                    Table table = new Table(Skins.COMODORE_SKIN);
                    table.setRound(false);
                    String text = message.text;
                    LabelStyle labelStyle = new LabelStyle(Skins.COMODORE_SKIN.getFont("flipped-with-border"), getColor(message));
                    labelStyle.font.setUseIntegerPositions(false);
                    Label label = new Label(text, labelStyle);
                    message.originalScale = message.kind == CombatMessage.Kind.STAB ? 1.3f : 1f;
                    float prefWidth = label.getPrefWidth();
                    label.setWrap(true);
                    label.setAlignment(Align.center);
                    Log.info("Width: " + prefWidth);
                    table.add(label).width(Math.min(prefWidth + 10, 200));
                    return table;
                }

            });

    public CombatRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(CombatMessage.class, Body.class, WorldPos.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());

        if (!player.hasCombatMessage()) {
            return;
        }
        CombatMessage combatMessage = player.getCombatMessage();
        Table label = messages.getUnchecked(combatMessage);
        combatMessage.offset = Interpolation.pow2OutInverse.apply(combatMessage.time / CombatMessage.DEFAULT_TIME) * CombatMessage.DEFAULT_OFFSET;
        if (combatMessage.offset < 0) {
            combatMessage.offset = 0;
        }

        combatMessage.time -= world.getDelta();
        if (combatMessage.time > 0) {
            Label lbl = (Label) label.getChild(0);
            if (combatMessage.scale > 0) {
                combatMessage.scale -= world.delta * 2;
                float scale = Interpolation.swingOut.apply(1 - combatMessage.scale);
                lbl.setFontScale(combatMessage.originalScale * scale);
            }
            if (combatMessage.time < CombatMessage.START_ALPHA) {
                lbl.getStyle().fontColor.premultiplyAlpha();
                lbl.getStyle().fontColor.a -= CombatMessage.DISAPEAR_SPEED * world.getDelta();
            }
            float width = label.getWidth();
            final float fontX = playerPos.x + (Tile.TILE_PIXEL_WIDTH - width) / 2;
            int bodyOffset = descriptorHandler.getBody(player.getBody().index).getHeadOffsetY();
            final float fontY = playerPos.y + combatMessage.offset + bodyOffset - 60 * SCALE
                    + label.getHeight();
            label.setPosition(fontX, fontY);
            label.draw(getBatch(), 1);
        } else {
            messages.invalidate(player.getCombatMessage());
            player.removeCombatMessage();
        }
    }

    private Color getColor(CombatMessage message) {
        Color color = Color.WHITE.cpy();
        switch (message.kind) {
            case MAGIC:
                color = Colors.MANA.cpy();
                break;
            case STAB:
                color = Colors.GREY.cpy();
                break;
            case ENERGY:
                color = Colors.YELLOW.cpy();
                break;
            case PHYSICAL:
                color = Colors.RED.cpy();
                break;
        }

        return color;
    }
}
