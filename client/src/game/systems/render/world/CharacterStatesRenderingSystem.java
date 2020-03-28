package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import entity.character.Character;
import game.utils.Pos2D;
import position.WorldPosOffsets;
import position.WorldPos;
import shared.util.Util;

@Wire(injectInherited = true)
public class CharacterStatesRenderingSystem extends RenderingSystem {

    public CharacterStatesRenderingSystem() {
        super(Aspect.all(Character.class, WorldPos.class));
    }

    @Override
    protected void process(E player) {
        if (isInAnyState(player)) {
            Pos2D playerPos = Pos2D.get(player).toScreen();
            if (player.hasWriting()) {

            }
        }
    }

    private boolean isInAnyState(E entity) {
        return entity.hasMeditating() || entity.hasWriting() || entity.hasResting();
    }
}
