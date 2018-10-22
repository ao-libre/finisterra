package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.systems.OrderedEntityProcessingSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.character.states.Immobile;
import position.Pos2D;
import position.WorldPos;

import java.util.Comparator;

import static ar.com.tamborindeguy.client.utils.Fonts.GM_NAME_FONT;
import static ar.com.tamborindeguy.client.utils.Fonts.layout;
import static com.artemis.E.E;

@Wire
public class StateRenderingSystem extends OrderedEntityProcessingSystem {

    private final SpriteBatch batch;
    private CameraSystem cameraSystem;

    public StateRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(WorldPos.class).one(Immobile.class));
        this.batch = batch;
    }

    private void drawMessage(int entityId) {
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();

        // search position
        Pos2D playerPos = Util.toScreen(E(entityId).worldPosPos2D());
        Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
        Pos2D screenPos = new Pos2D(cameraPos.x - playerPos.x, cameraPos.y - playerPos.y);
        // draw [P] in cyan color
        layout.setText(GM_NAME_FONT, "[P]");
        final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x - (Tile.TILE_PIXEL_WIDTH) / 2;
        final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y - (layout.height) / 2 + 15;
        GM_NAME_FONT.draw(batch, layout, fontX, fontY);
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        int entityId = e.getId();
        int currentPlayer = GameScreen.getPlayer();
        if (currentPlayer == entityId) {
            drawMessage(entityId);
        } else if (E(entityId).hasClan() && E(currentPlayer).hasClan()) {
            String entityClan = E(entityId).getClan().name;
            String playerClan = E(currentPlayer).getClan().name;
            if (entityClan.equals(playerClan)) {
                drawMessage(entityId);
            }
        }
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
