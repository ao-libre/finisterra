package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import component.entity.character.parts.Body;
import component.entity.world.CombatMessage;
import component.position.WorldPos;
import game.systems.render.BatchRenderingSystem;
import game.systems.resources.DescriptorsSystem;
import game.ui.WidgetFactory;
import game.utils.Pos2D;
import game.utils.Skins;
import org.jetbrains.annotations.NotNull;
import shared.model.map.Tile;

import java.util.concurrent.TimeUnit;

@Wire(injectInherited = true)
public class CombatRenderingSystem extends RenderingSystem {

    public static final float VELOCITY = 1f;
    private DescriptorsSystem descriptorsSystem;
    private BatchRenderingSystem batchRenderingSystem;
    private LoadingCache<CombatMessage, Table> messages = CacheBuilder
            .newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<CombatMessage, Table>() {
                @Override
                public Table load(@NotNull CombatMessage message) {
                    Table table = new Table(Skins.COMODORE_SKIN);
                    table.setRound(false);
                    Label label = WidgetFactory.createCombatLabel(message);
                    message.originalScale = message.kind == CombatMessage.Kind.STAB ? 1.3f : 1f;
                    float prefWidth = label.getPrefWidth();
                    label.setWrap(true);
                    label.setAlignment(Align.center);
                    Log.debug("Width: " + prefWidth);
                    table.add(label).width(Math.min(prefWidth + 10, 200));
                    return table;
                }

            });

    public CombatRenderingSystem() {
        super(Aspect.all(CombatMessage.class, Body.class, WorldPos.class));
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Pos2D.get(player).toScreen();

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
            int bodyOffset = descriptorsSystem.getBody(player.getBody().index).getHeadOffsetY();
            final float fontY = playerPos.y + combatMessage.offset + bodyOffset - 60 * SCALE
                    + label.getHeight();
            label.setPosition(fontX, fontY);
            batchRenderingSystem.addTask((batch -> label.draw(batch, 1)));
        } else {
            messages.invalidate(player.getCombatMessage());
            player.removeCombatMessage();
        }
    }

}
