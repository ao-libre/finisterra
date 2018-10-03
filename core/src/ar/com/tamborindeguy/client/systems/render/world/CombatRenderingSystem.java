package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.systems.OrderedEntityProcessingSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.CombatMessage;
import position.Pos2D;

import java.util.Comparator;

import static ar.com.tamborindeguy.client.systems.render.world.DialogRenderingSystem.ALPHA_TIME;
import static ar.com.tamborindeguy.client.utils.Fonts.*;
import static com.artemis.E.E;

public class CombatRenderingSystem extends OrderedEntityProcessingSystem {

    private SpriteBatch batch;
    private CameraSystem cameraSystem;

    public CombatRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(CombatMessage.class, Pos2D.class));
        this.batch = batch;
    }

    @Override
    protected void process(Entity e) {
        E player = E(e);
        Pos2D playerPos = Util.toScreen(player.getPos2D());
        Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
        Pos2D screenPos = new Pos2D(cameraPos.x - playerPos.x, cameraPos.y - playerPos.y);
        CombatMessage combatMessage = player.getCombatMessage();
        combatMessage.time -= world.getDelta();
        if (combatMessage.time > 0) {
            Color copy = DIALOG_FONT.getColor().cpy();
            if (combatMessage.time < ALPHA_TIME) {
                combatMessage.alpha = combatMessage.time / ALPHA_TIME;
                COMBAT_FONT.getColor().a = combatMessage.alpha;
            }
            cameraSystem.guiCamera.update();
            batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
            batch.begin();

            dialogLayout.setText(COMBAT_FONT, combatMessage.text);
            float width = dialogLayout.width;
            dialogLayout.setText(COMBAT_FONT, combatMessage.text, DIALOG_FONT.getColor(), width, Align.center, true);
            final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x - (width / 2) - (Tile.TILE_PIXEL_WIDTH / 2) - 4;
            final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y + 60 + dialogLayout.height;
            COMBAT_FONT.draw(batch, dialogLayout, fontX, fontY);

            batch.end();
            COMBAT_FONT.setColor(copy);
        } else {
            player.removeCombatMessage();
        }
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
