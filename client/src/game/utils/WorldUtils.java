package game.utils;

import game.screens.GameScreen;
import game.systems.camera.CameraSystem;
import shared.util.Util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import entity.Heading;
import physics.AOPhysics;
import position.Pos2D;
import position.WorldPos;

import java.util.Optional;

import static com.artemis.E.E;

public class WorldUtils {

    public static Optional<WorldPos> mouseToWorldPos() {
        CameraSystem camera = GameScreen.getWorld().getSystem(CameraSystem.class);
        WorldPos worldPos = E(GameScreen.getPlayer()).getWorldPos();
        int map = worldPos.map;

        // Mouse coordinates in world
        Vector3 screenPos = camera.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        WorldPos value = Util.toWorld(new Pos2D(screenPos.x, screenPos.y));
        value.map = map;
        return Optional.of(value);
    }

    public static int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }
}
