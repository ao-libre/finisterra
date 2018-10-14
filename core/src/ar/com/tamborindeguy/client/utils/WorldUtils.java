package ar.com.tamborindeguy.client.utils;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
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
        // works?
        WorldPos value = Util.toWorld(new Pos2D(screenPos.x, screenPos.y));
        value.map = map;
        return Optional.of(value);
    }
//    public static Optional<WorldPos> mouseToWorldPos() {
//        WorldPos worldPos = E(GameScreen.getPlayer()).getWorldPos();
//        int map = worldPos.map;
//        int realDistanceX = (Gdx.graphics.getWidth() / 2) - Gdx.input.getX();
//        int realDistanceY = (Gdx.graphics.getHeight() / 2) - Gdx.input.getY() - 32;
//        int distanceX = Math.round((realDistanceX / Tile.TILE_PIXEL_WIDTH));
//        int distanceY = Math.round((realDistanceY / Tile.TILE_PIXEL_HEIGHT));
//        return Optional.of(new WorldPos(worldPos.x - distanceX, worldPos.y - distanceY, map));
//    }
}
