package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.character.states.Immobile;
import game.screens.GameScreen;
import game.systems.OrderedEntityProcessingSystem;
import game.systems.camera.CameraSystem;
import game.utils.Fonts;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;
import shared.util.Util;

import java.util.Comparator;

import static com.artemis.E.E;

@Wire
public class StateRenderingSystem extends OrderedEntityProcessingSystem {

    private final SpriteBatch batch;
    private CameraSystem cameraSystem;

    public StateRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(WorldPos.class).one(Immobile.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.camera.update();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();

    }

    @Override
    protected void end() {
        batch.end();
    }

    private void drawMessage(int entityId) {
        // search position
        Pos2D playerPos = Util.toScreen(E(entityId).worldPosPos2D());
        // draw [P] in cyan color
        Fonts.layout.setText(Fonts.GM_NAME_FONT, "[P]");
        final float fontX = playerPos.x - (Tile.TILE_PIXEL_WIDTH) / 2;
        final float fontY = playerPos.y + (Fonts.layout.height) / 2 - 15;
        Fonts.GM_NAME_FONT.draw(batch, Fonts.layout, fontX, fontY);
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
