package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import entity.character.parts.Body;
import entity.world.Dialog;
import game.handlers.DescriptorHandler;
import game.utils.Fonts;
import game.utils.Skins;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.concurrent.TimeUnit;

@Wire(injectInherited = true)
public class DialogRenderingSystem extends RenderingSystem {

    private DescriptorHandler descriptorHandler;

    private static final int ALPHA_TIME = 2;
    private static final int MAX_LENGTH = (int) (120 * SCALE);
    private static final int DISTANCE_TO_TOP = (int) (5 * SCALE);
    private static final float TIME = 0.3f;
    private static final float VELOCITY = DISTANCE_TO_TOP / TIME;

    private LoadingCache<Dialog, Actor> labels = CacheBuilder
            .newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Dialog, Actor>() {
                @Override
                public Actor load(Dialog dialog) {
                    Table table = new Table(Skins.COMODORE_SKIN);
                    String text = dialog.text;
                    Label label = new Label(text, Skins.COMODORE_SKIN, "speech-bubble");
                    label.getGlyphLayout().setText(label.getStyle().font, text);
                    label.getStyle().font.setUseIntegerPositions(false);
                    float prefWidth = label.getGlyphLayout().width;
                    label.getStyle().font = Fonts.WHITE_FONT;
                    label.getColor().a = 0.8f;
                    label.setWrap(true);
                    label.setAlignment(Align.center);
                    Log.info("Width: " + prefWidth);
                    table.add(label).width(Math.min(prefWidth + 10, 200));
                    return table;
                }

            });

    public DialogRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Dialog.class, Body.class, WorldPos.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
        Dialog dialog = player.getDialog();
        dialog.time -= world.getDelta();
        if (dialog.time > 0) {
            if (dialog.text != null && dialog.text.length() > 0) {
                if (dialog.kind.equals(Dialog.Kind.MAGIC_WORDS)) {
                    drawFont(player, playerPos, dialog);
                } else {
                    drawBubble(player, playerPos, dialog);
                }
            }
        } else {
            labels.invalidate(dialog);
            player.removeDialog();
        }
    }

    private void drawBubble(E player, Pos2D playerPos, Dialog dialog) {
        Actor label = labels.getUnchecked(dialog);
        final float x = playerPos.x + (Tile.TILE_PIXEL_WIDTH - label.getWidth()) / 2;

        float up = Dialog.DEFAULT_TIME - dialog.time <= TIME ? (Dialog.DEFAULT_TIME - dialog.time) * VELOCITY : DISTANCE_TO_TOP;
        float offsetY = descriptorHandler.getBody(player.getBody().index).getHeadOffsetY() * SCALE;
        final float y = playerPos.y - 55 * SCALE + offsetY - up + label.getHeight();

        label.setPosition(x, y);

        if (dialog.time < ALPHA_TIME) {
            dialog.alpha = dialog.time / ALPHA_TIME;
        }
        label.setRotation(180);
        label.draw(getBatch(), dialog.alpha);
    }

    private void drawFont(E player, Pos2D playerPos, Dialog dialog) {
        BitmapFont font = dialog.kind.equals(Dialog.Kind.MAGIC_WORDS) ? Fonts.MAGIC_FONT : Fonts.DIALOG_FONT;
        Color copy = font.getColor().cpy();
        if (dialog.time < ALPHA_TIME) {
            dialog.alpha = dialog.time / ALPHA_TIME;
            font.getColor().a = dialog.alpha;
        }

        Fonts.dialogLayout.setText(font, dialog.text);
        float width = Math.min(Fonts.dialogLayout.width, MAX_LENGTH);
        Fonts.dialogLayout.setText(font, dialog.text, font.getColor(), width, Align.center | Align.top, true);
        final float fontX = playerPos.x + (Tile.TILE_PIXEL_WIDTH - width) / 2;
        float up = Dialog.DEFAULT_TIME - dialog.time <= TIME ? (Dialog.DEFAULT_TIME - dialog.time) * VELOCITY : DISTANCE_TO_TOP;
        float offsetY = descriptorHandler.getBody(player.getBody().index).getHeadOffsetY() * SCALE;
        final float fontY = playerPos.y - 70 * SCALE + offsetY - up + Fonts.dialogLayout.height;
        font.draw(getBatch(), Fonts.dialogLayout, fontX, fontY);
        font.setColor(copy);
    }

}
