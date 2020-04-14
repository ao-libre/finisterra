package game.systems.render.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.states.Immobile;
import game.systems.PlayerSystem;
import component.position.WorldPos;

@Wire(injectInherited = true)
public class StateRenderingSystem extends RenderingSystem {

    private PlayerSystem playerSystem;

    public StateRenderingSystem() {
        super(Aspect.all(WorldPos.class).one(Immobile.class));
    }

    private void drawMessage(int entityId) {
        // search component.position
        // draw [P] in cyan color
        // TODO
    }

    @Override
    protected void process(E e) {
        // TODO move to server
//        int entityId = e.id();
//        E playerEntity = playerSystem.get();
//        int currentPlayer = playerEntity.id();
//        if (currentPlayer == entityId) {
//            drawMessage(entityId);
//        } else if (e.hasClan() && playerEntity.hasClan()) {
//            String entityClan = E(entityId).getClan().name;
//            String playerClan = playerEntity.getClan().name;
//            if (entityClan.equals(playerClan)) {
//                drawMessage(entityId);
//            }
//        }
    }
}
