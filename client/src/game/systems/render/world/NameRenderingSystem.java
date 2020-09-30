package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import component.entity.character.info.Name;
import component.entity.character.status.Health;
import component.entity.character.status.Mana;
import component.position.WorldPos;
import game.systems.render.BatchRenderingSystem;
import game.ui.WidgetFactory;
import game.utils.Colors;
import game.utils.Pos2D;
import game.utils.Skins;
import org.jetbrains.annotations.NotNull;
import shared.interfaces.Hero;
import shared.model.map.Tile;

import static com.artemis.E.E;

@Wire(injectInherited = true)
public class NameRenderingSystem extends RenderingSystem {

    @NotNull
    private Label createLabel(String name) {
        Label label = WidgetFactory.createFlippedLabel(name);
        label.getStyle().font.setUseIntegerPositions(false);
        return label;
    }

    private ProgressBar createHP(Health health) {
        ProgressBar bar = WidgetFactory.createProgressBar(WidgetFactory.ProgressBars.INGAME_HP);
        bar.setRange(0, health.max);
        bar.setValue(health.min);
        bar.setAnimateInterpolation(Interpolation.fastSlow);
        bar.setAnimateDuration(0.3f);
        bar.setRound(false);
        return bar;
    }

    private ProgressBar createMana(Mana mana) {
        ProgressBar bar = WidgetFactory.createProgressBar(WidgetFactory.ProgressBars.INGAME_MANA);
        bar.setRange(0, mana.max);
        bar.setValue(mana.min);
        bar.setAnimateInterpolation(Interpolation.fastSlow);
        bar.setAnimateDuration(0.3f);
        bar.setRound(false);
        return bar;
    }

    private BatchRenderingSystem batchRenderingSystem;

    public NameRenderingSystem() {
        super(Aspect.all(WorldPos.class, Name.class));
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Pos2D.get(player).toScreen();

        if (player.hasDisplay()) { // update
            if (player.hasMana()) {
                player.displayMana(player.manaMin(), player.manaMax());
            }
            if (player.hasHealth()) {
                player.displayHp(player.healthMin(), player.healthMax());
            }
        } else { // create
            player.display();
            if (player.hasMana()) {
                player.displayAddMana(createMana(player.getMana()));
            }
            if (player.hasHealth()) {
                player.displayAddHp(createHP(player.getHealth()));
            }
            Label nameLabel = createLabel(player.nameText());
            float prefWidth = nameLabel.getPrefWidth();
            player.displayAddLabel(nameLabel, prefWidth);
        }
        Table table = player.displayDisplay();
        float nameY = drawName(player, playerPos, table);
    }

    private float drawName(E player, Pos2D screenPos, Table table) {
        final float fontX = screenPos.x + ((Tile.TILE_PIXEL_WIDTH - table.getWidth()) / 2);
        final float fontY = screenPos.y + 15;
        table.act(getWorld().getDelta());
        batchRenderingSystem.addTask(batch -> {
                    Color color = setColor(player, player.displayLabel());
                    table.setPosition(fontX, fontY);
                    table.draw(batch, 1);
                    player.displayLabel().getStyle().fontColor = color;
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
