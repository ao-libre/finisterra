package game.systems.camera;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.EBag;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import component.camera.AOCamera;
import component.camera.Focused;
import component.position.WorldPos;
import game.utils.Pos2D;

import static com.artemis.E.E;

@Wire
public class CameraFocusSystem extends IteratingSystem {

    public CameraFocusSystem() {
        super(Aspect.all(Focused.class, WorldPos.class));
    }

    @Override
    protected void process(int player) {
        EBag cameras = E.withComponent(AOCamera.class);
        if (cameras.iterator().hasNext()) {
            E playerEntity = E(player);
            Pos2D pos = Pos2D.get(playerEntity).toScreen();
            cameras.iterator().next()
                    .worldPosOffsetsX(pos.x)
                    .worldPosOffsetsY(pos.y);
        }
    }

}
