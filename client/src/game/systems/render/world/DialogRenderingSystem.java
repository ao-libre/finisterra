package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import component.entity.character.parts.Body;
import component.entity.world.Dialog;
import component.entity.world.Dialog.Kind;
import game.systems.resources.DescriptorsSystem;
import game.systems.render.BatchRenderingSystem;
import game.utils.Colors;
import game.utils.Pos2D;
import game.utils.Skins;
import org.jetbrains.annotations.NotNull;
import component.position.WorldPos;
import shared.model.map.Tile;

import java.util.concurrent.TimeUnit;

@Wire(injectInherited = true)
public class DialogRenderingSystem extends RenderingSystem {

    private static final int ALPHA_TIME = 2;
    private static final int MAX_LENGTH = (int) (120 * SCALE);
    private static final int DISTANCE_TO_TOP = (int) (5 * SCALE);
    private static final float TIME = 0.3f;
    private static final float VELOCITY = DISTANCE_TO_TOP / TIME * SCALE;
    private final LoadingCache<Dialog, Table> labels = CacheBuilder
            .newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Table load(@NotNull Dialog dialog) {
                    Table table = new Table(Skins.COMODORE_SKIN);
                    table.setRound(false);
                    String text = dialog.text;
                    Label label = new Label(text, Skins.COMODORE_SKIN, dialog.kind == Kind.MAGIC_WORDS ? "flipped" : "speech-bubble");
                    label.getStyle().font.setUseIntegerPositions(false);
                    float prefWidth = label.getPrefWidth();
                    label.setWrap(true);
                    label.setAlignment(Align.center);
                    Log.debug("Width: " + prefWidth);
                    table.add(label).width(Math.min(prefWidth + 20, MAX_LENGTH));
                    return table;
                }
            });
    private DescriptorsSystem descriptorsSystem;
    private BatchRenderingSystem batchRenderingSystem;

    public DialogRenderingSystem() {
        super(Aspect.all(Dialog.class, Body.class, WorldPos.class));
    }

    private Color setColor(Dialog dialog, Label label) {
        Color prev = label.getStyle().fontColor.cpy();
        label.setColor(dialog.kind == Kind.MAGIC_WORDS ? Colors.MANA : Color.WHITE);
        return prev;
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Pos2D.get(player).toScreen();
        Dialog dialog = player.getDialog();
        dialog.time -= world.getDelta();
        if (dialog.time > 0) {
            if (dialog.text != null && dialog.text.length() > 0) {
                drawBubble(player, playerPos, dialog);
            }
        } else {
            labels.invalidate(dialog);
            player.removeDialog();
        }
    }

    private void drawBubble(E player, Pos2D playerPos, Dialog dialog) {
        Table label = labels.getUnchecked(dialog);
        final float x = playerPos.x + (Tile.TILE_PIXEL_WIDTH - label.getWidth()) / 2;
        float up = (Dialog.DEFAULT_TIME - dialog.time) * VELOCITY;
        up = Math.min(up, DISTANCE_TO_TOP);
        float offsetY = descriptorsSystem.getBody(player.getBody().index).getHeadOffsetY() * SCALE;
        final float y = playerPos.y - 55 * SCALE + offsetY - up + label.getHeight();

        label.setPosition(x, y);
        Label child = (Label) label.getChild(0);
        Color color = setColor(dialog, child);
        if (dialog.time < ALPHA_TIME) {
            dialog.alpha = dialog.time / ALPHA_TIME;
        }
        batchRenderingSystem.addTask(batch -> label.draw(batch, dialog.alpha));
        child.getStyle().fontColor = color;
    }
}
