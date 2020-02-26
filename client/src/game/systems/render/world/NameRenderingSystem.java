package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureArraySpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import entity.character.Character;
import entity.character.info.Name;
import game.utils.Colors;
import game.utils.Skins;
import org.jetbrains.annotations.NotNull;
import position.Pos2D;
import position.WorldPos;
import shared.interfaces.Hero;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.concurrent.TimeUnit;

import static com.artemis.E.E;

@Wire(injectInherited = true)
public class NameRenderingSystem extends RenderingSystem {

    private final LoadingCache<Integer, Table> names = CacheBuilder
            .newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, Table>() {
                @Override
                public Table load(@NotNull Integer integer) {
                    Table table = new Table(Skins.COMODORE_SKIN);
                    table.setRound(false);
                    E e = E(integer);
                    String text = e.getName().text;
                    Label label = new Label(text, Skins.COMODORE_SKIN, "flipped");
                    label.getStyle().font.setUseIntegerPositions(false);
                    label.setFontScale(1.1f);
                    float prefWidth = label.getPrefWidth();
                    label.setWrap(true);
                    label.setAlignment(Align.center);
                    Log.info("Width: " + prefWidth);
                    table.add(label).width(Math.min(prefWidth + 20, 200));
                    return table;
                }


            });

    public NameRenderingSystem(TextureArraySpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class, Name.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());

        float nameY = drawName(player, playerPos);
        drawClanName(player, playerPos, nameY);
    }

    private float drawName(E player, Pos2D screenPos) {
        Table nameLabel = names.getUnchecked(player.id());

        final float fontX = screenPos.x + ((Tile.TILE_PIXEL_WIDTH - nameLabel.getWidth()) / 2);
        final float fontY = screenPos.y + 10;
        Label label = (Label) nameLabel.getChild(0);
        label.getStyle().font.setUseIntegerPositions(false);
        Color color = setColor(player, label);
        nameLabel.setPosition(fontX, fontY);
        nameLabel.draw(getBatch(), 1);
        label.getStyle().fontColor = color;
        return fontY;
    }

    private void drawClanName(E player, Pos2D screenPos, float nameY) {
        String clanOrHero = null;

        if (player.hasClan() && !player.getClan().name.isEmpty()) {
            clanOrHero = player.getClan().name;
        } else if (player.hasCharHero()) {
            clanOrHero = Hero.values()[player.getCharHero().heroId].name();
        }
    }

    private Color setColor(E player, Label label) {
        Color previous = new Color(label.getStyle().fontColor);
        label.getStyle().fontColor = player.hasCriminal() ? Colors.CRIMINAL : Colors.CITIZEN;
        return previous;
    }

}
