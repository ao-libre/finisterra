package game.utils;

import com.artemis.E;
import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import entity.character.states.Heading;
import game.screens.GameScreen;
import game.screens.WorldScreen;
import game.systems.camera.CameraSystem;
import physics.AOPhysics;
import position.Pos2D;
import position.WorldPos;
import shared.util.Util;

import java.util.Optional;

import static com.artemis.E.E;

public class WorldUtils {

    public static Optional<World> getWorld() {
        Game game = (Game) Gdx.app.getApplicationListener();
        Screen screen = game.getScreen();
        if (screen instanceof WorldScreen) {
            return Optional.of(((WorldScreen) screen).getWorld());
        }
        return Optional.empty();
    }

    public static Optional<WorldPos> mouseToWorldPos() {
        return getWorld().map(world -> {
            CameraSystem camera = world.getSystem(CameraSystem.class);
            if (GameScreen.getPlayer() < 0) {
                return null;
            }
            final E e = E(GameScreen.getPlayer());
            if (e == null || !e.hasWorldPos()) {
                return null;
            }
            WorldPos worldPos = e.getWorldPos();
            int map = worldPos.map;

            // Mouse coordinates in world
            Vector3 screenPos = camera.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            WorldPos value = Util.toWorld(new Pos2D(screenPos.x, screenPos.y));
            value.map = map;
            return value;
        });
    }

    public static int getHeading(AOPhysics.Movement movement) {
        return movement == AOPhysics.Movement.UP ? Heading.HEADING_NORTH : movement == AOPhysics.Movement.DOWN ? Heading.HEADING_SOUTH : movement == AOPhysics.Movement.LEFT ? Heading.HEADING_WEST : Heading.HEADING_EAST;
    }

    public static int distance(WorldPos pos1, WorldPos pos2) {
        if (pos1 == null || pos2 == null) {
            return -1;
        }
        if (pos1.map != pos2.map) {
            return -1;
        }
        return Math.abs(getDistanceX(pos1, pos2) + (pos1.y - pos2.y));
    }

    public static int getDistanceX(WorldPos pos1, WorldPos pos2) {
        return pos1.x - pos2.x;
    }
}