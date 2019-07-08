package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entity.character.Character;
import position.Pos2D;
import position.WorldPos;
import shared.util.Util;

@Wire(injectInherited = true)
public class CharacterStatesRenderingSystem extends RenderingSystem {

    public CharacterStatesRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class), batch, CameraKind.WORLD);
    }

    @Override
    protected void process(E player) {
        if (isInAnyState(player)) {
            Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
            if (player.hasWriting()) {

            }
        }
    }

    private boolean isInAnyState(E entity) {
        return entity.hasMeditating() || entity.hasWriting() || entity.hasResting();
    }
}