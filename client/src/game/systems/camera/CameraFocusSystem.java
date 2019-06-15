package game.systems.camera;

import camera.AOCamera;
import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.EBag;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import position.Pos2D;
import position.WorldPos;
import shared.util.Util;

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
            E camera = cameras.iterator().next();
            Pos2D cameraPos = camera.getPos2D();
            Pos2D pos = Util.toScreen(E(player).worldPosPos2D());
            cameraPos.x = pos.x;
            cameraPos.y = pos.y;
        }
    }

}
