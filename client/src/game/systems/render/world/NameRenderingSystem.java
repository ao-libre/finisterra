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
import component.entity.character.info.Name;
import component.position.WorldPos;
import game.systems.render.BatchRenderingSystem;
import game.utils.Colors;
import game.utils.Pos2D;
import game.utils.Skins;
import org.jetbrains.annotations.NotNull;
import shared.interfaces.Hero;
import shared.model.map.Tile;

import java.util.concurrent.TimeUnit;

import static com.artemis.E.E;

@Wire(injectInherited = true)
public class NameRenderingSystem extends RenderingSystem {

    private BatchRenderingSystem batchRenderingSystem;
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
                    Log.debug("Width: " + prefWidth);
                    table.add(label).width(Math.min(prefWidth + 20, 200));
                    return table;
                }


            });

    public NameRenderingSystem() {
        super(Aspect.all(WorldPos.class, Name.class));
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Pos2D.get(player).toScreen();

        float nameY = drawName(player, playerPos);
        drawClanName(player, playerPos, nameY);
    }

    private float drawName(E player, Pos2D screenPos) {
        Table nameLabel = names.getUnchecked(player.id());

        final float fontX = screenPos.x + ((Tile.TILE_PIXEL_WIDTH - nameLabel.getWidth()) / 2);
        final float fontY = screenPos.y + 10;
        Label label = (Label) nameLabel.getChild(0);
        label.getStyle().font.setUseIntegerPositions(false);
        batchRenderingSystem.addTask(batch -> {
                    Color color = setColor(player, label);
                    nameLabel.setPosition(fontX, fontY);
                    nameLabel.draw(batch, 1);
                    label.getStyle().fontColor = color;
                }
        );
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
        label.getStyle().fontColor = player.hasCriminal() ? Colors.CRIMINAL : player.hasNPC() ? Colors.GREY : Colors.CITIZEN;
        return previous;
    }

}
