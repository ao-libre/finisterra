package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureArraySpriteBatch;
import entity.character.states.Immobile;
import game.screens.GameScreen;
import position.Pos2D;
import position.WorldPos;
import shared.util.Util;

import static com.artemis.E.E;

@Wire(injectInherited = true)
public class StateRenderingSystem extends RenderingSystem {

    public StateRenderingSystem(TextureArraySpriteBatch batch) {
        super(Aspect.all(WorldPos.class).one(Immobile.class), batch, CameraKind.WORLD);
    }

    private void drawMessage(int entityId) {
        // search position
        Pos2D playerPos = Util.toScreen(E(entityId).worldPosPos2D());
        // draw [P] in cyan color
        // TODO
    }

    @Override
    protected void process(E e) {
        int entityId = e.id();
        int currentPlayer = GameScreen.getPlayer();
        if (currentPlayer == entityId) {
            drawMessage(entityId);
        } else if (e.hasClan() && E(currentPlayer).hasClan()) {
            String entityClan = E(entityId).getClan().name;
            String playerClan = E(currentPlayer).getClan().name;
            if (entityClan.equals(playerClan)) {
                drawMessage(entityId);
            }
        }
    }
}