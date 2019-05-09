package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.character.Character;
import entity.character.info.Name;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.utils.Fonts;
import position.Pos2D;
import position.WorldPos;
import shared.interfaces.Hero;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;

import static com.artemis.E.E;

@Wire(injectInherited=true)
public class NameRenderingSystem extends RenderingSystem {

    public NameRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class, Name.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E player) {
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());

        float nameY = drawName(player, playerPos);
        drawClanName(player, playerPos, nameY);
    }

    private float drawName(E player, Pos2D screenPos) {
        BitmapFont font =
                player.hasGM() ? Fonts.GM_NAME_FONT :
                        player.hasLevel() && player.getLevel().level < 13 ? Fonts.NEWBIE_NAME_FONT :
                                player.hasCriminal() ? Fonts.CRIMINAL_NAME_FONT : Fonts.CITIZEN_NAME_FONT;
        Fonts.layout.setText(font, player.getName().text);
        final float fontX = screenPos.x + ((Tile.TILE_PIXEL_WIDTH - Fonts.layout.width) / 2);
        final float fontY = screenPos.y;

        font.draw(getBatch(), Fonts.layout, fontX, fontY);
        return fontY;
    }

    private void drawClanName(E player, Pos2D screenPos, float nameY) {
        String clanOrHero = null;
        if (player.hasClan() && !player.getClan().name.isEmpty()) {
            clanOrHero = player.getClan().name;
        } else if (player.hasCharHero()){
             clanOrHero = Hero.values()[player.getCharHero().heroId].name();
        }
        if (clanOrHero == null) {
            return;
        }
        Fonts.layout.setText(Fonts.CLAN_FONT, "<" + clanOrHero + ">");
        final float fontX = screenPos.x + ((Tile.TILE_PIXEL_WIDTH - Fonts.layout.width) / 2);
        final float fontY = nameY + Fonts.layout.height + 5;
        Fonts.CLAN_FONT.draw(getBatch(), Fonts.layout, fontX, fontY);
    }

}
