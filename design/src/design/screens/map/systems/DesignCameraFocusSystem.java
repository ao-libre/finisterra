package design.screens.map.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.EBag;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.camera.AOCamera;
import component.camera.Focused;
import component.position.WorldPos;
import game.utils.Pos2D;

@Wire
public class DesignCameraFocusSystem extends IteratingSystem {

    public DesignCameraFocusSystem() {
        super( Aspect.all( Focused.class, WorldPos.class));
    }

    @Override
    protected void process(int player) {
        EBag cameras = E.withComponent( AOCamera.class);
        if (cameras.iterator().hasNext()) {
            E playerEntity = E.E(player);
            Pos2D pos = Pos2D.get(playerEntity).toScreen();
            cameras.iterator().next()
                    .worldPosOffsetsX(pos.x)
                    .worldPosOffsetsY(pos.y);
        }
    }

}
