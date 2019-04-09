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

@Wire
public class NameRenderingSystem extends OrderedEntityProcessingSystem {

    private SpriteBatch batch;
    private CameraSystem cameraSystem;

    public NameRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class, Name.class));
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

        float nameY = drawName(player, screenPos);
        drawClanName(player, screenPos, nameY);
    }

    private float drawName(E player, Pos2D screenPos) {
        BitmapFont font =
                player.hasGM() ? Fonts.GM_NAME_FONT :
                        player.hasLevel() && player.getLevel().level < 13 ? Fonts.NEWBIE_NAME_FONT :
                                player.hasCriminal() ? Fonts.CRIMINAL_NAME_FONT : Fonts.CITIZEN_NAME_FONT;
        Fonts.layout.setText(font, player.getName().text);
        final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x + ((Tile.TILE_PIXEL_WIDTH - Fonts.layout.width) / 2);
        final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y;
        font.draw(batch, Fonts.layout, fontX, fontY);
        return fontY;
    }

    private void drawClanName(E player, Pos2D screenPos, float nameY) {
        String clanOrHero = Hero.getHeros().get(player.getCharHero().heroId).name();
        if (player.hasClan() && !player.getClan().name.isEmpty()) {
            clanOrHero = player.getClan().name;
        }
        Fonts.layout.setText(Fonts.CLAN_FONT, "<" + clanOrHero + ">");
        final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x + ((Tile.TILE_PIXEL_WIDTH - Fonts.layout.width) / 2);
        final float fontY = nameY - Fonts.layout.height - 5;
        Fonts.CLAN_FONT.draw(batch, Fonts.layout, fontX, fontY);
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
