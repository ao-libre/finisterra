package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.handlers.DescriptorHandler;
import ar.com.tamborindeguy.client.systems.OrderedEntityProcessingSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.descriptors.BodyDescriptor;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.minlog.Log;
import entity.Body;
import entity.CombatMessage;
import position.Pos2D;
import position.WorldPos;

import java.util.Comparator;

import static ar.com.tamborindeguy.client.systems.render.world.DialogRenderingSystem.ALPHA_TIME;
import static ar.com.tamborindeguy.client.utils.Fonts.*;
import static com.artemis.E.E;

public class CombatRenderingSystem extends OrderedEntityProcessingSystem {

    private SpriteBatch batch;
    private CameraSystem cameraSystem;

    public CombatRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(CombatMessage.class, Body.class, WorldPos.class));
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

        if (!player.hasCombatMessage()) {
            // TODO bug here, sometimes getter return null
            return;
        }
        CombatMessage combatMessage = player.getCombatMessage();
        Log.info("Combate message: " + combatMessage);
        combatMessage.offset -= getWorld().getDelta() * combatMessage.time * 15.0f;
        if (combatMessage.offset < 0) {
            combatMessage.offset = 0;
        }

        combatMessage.time -= world.getDelta();
        if (combatMessage.time > 0) {

            BitmapFont font = combatMessage.kind == CombatMessage.Kind.PHYSICAL ? COMBAT_FONT : MAGIC_COMBAT_FONT;
            Color copy = font.getColor().cpy();
            if (combatMessage.time < CombatMessage.DEFAULT_ALPHA) {
                combatMessage.alpha = combatMessage.time / CombatMessage.DEFAULT_ALPHA;
                font.getColor().a = combatMessage.alpha;
            }

            dialogLayout.setText(font, combatMessage.text);
            float width = dialogLayout.width;
            dialogLayout.setText(font, combatMessage.text, font.getColor(), width, Align.center, true);
            final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x - (Tile.TILE_PIXEL_WIDTH + dialogLayout.width) / 2;
            int bodyOffset = 20 - DescriptorHandler.getBody(player.getBody().index).getHeadOffsetY();
            final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y - combatMessage.offset + bodyOffset + dialogLayout.height; //40 should be the Y offset of the entity
            font.draw(batch, dialogLayout, fontX, fontY);
            font.setColor(copy);
        } else {
            player.removeCombatMessage();
        }
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E(entity).getWorldPos().y);
    }
}
