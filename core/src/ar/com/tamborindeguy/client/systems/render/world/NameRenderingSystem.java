package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.systems.OrderedEntityProcessingSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.character.Character;
import entity.character.info.Name;
import position.Pos2D;

import java.util.Comparator;

import static ar.com.tamborindeguy.client.utils.Fonts.*;
import static com.artemis.E.E;

@Wire
public class NameRenderingSystem extends OrderedEntityProcessingSystem {

    private SpriteBatch batch;
    private CameraSystem cameraSystem;

    public NameRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, Pos2D.class, Name.class));
        this.batch = batch;
    }

    @Override
    protected void process(Entity e) {
        E player = E(e);
        Pos2D playerPos = Util.toScreen(player.getPos2D());
        Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
        Pos2D screenPos = new Pos2D(cameraPos.x - playerPos.x, cameraPos.y - playerPos.y);
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();

        float nameY = drawName(player, screenPos);
        drawClanName(player, screenPos, nameY);

        batch.end();
    }

    private float drawName(E player, Pos2D screenPos) {
        BitmapFont font =
                player.hasGM() ? GM_NAME_FONT :
                player.getLevel().level < 13 ? NEWBIE_NAME_FONT :
                player.hasCriminal() ? CRIMINAL_NAME_FONT : CITIZEN_NAME_FONT;
        layout.setText(font, player.getName().text);
        final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x - (Tile.TILE_PIXEL_WIDTH + layout.width) / 2 - 4;
        final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y - (layout.height) / 2;
        font.draw(batch, layout, fontX, fontY);
        return fontY;
    }

    private void drawClanName(E player, Pos2D screenPos, float nameY) {
        if (player.hasClan() && !player.getClan().name.isEmpty()) {
            layout.setText(CLAN_FONT, "<" + player.getClan().name + ">");
            final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x - (Tile.TILE_PIXEL_WIDTH + layout.width) / 2 - 4;
            final float fontY = nameY - layout.height - 5;
            CLAN_FONT.draw(batch, layout, fontX, fontY);
        }
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
