package shared.util;

import component.physics.AOPhysics;
import component.position.WorldPos;
import component.position.WorldPosOffsets;
import shared.model.map.Tile;

public class WorldPosConversion {

    public static WorldPosOffsets toScreen(WorldPos worldPos) {
        return new WorldPosOffsets(worldPos.x * Tile.TILE_PIXEL_WIDTH,
                worldPos.y * Tile.TILE_PIXEL_HEIGHT);
    }

    public static WorldPos toWorld(WorldPosOffsets pos) {
        return toWorld(pos.x, pos.y);
    }

    public static WorldPos toWorld(float x, float y) {
        return new WorldPos((int) (x / Tile.TILE_PIXEL_WIDTH), (int) (y / Tile.TILE_PIXEL_HEIGHT));
    }

    public static WorldPos getNextPos(WorldPos pos, AOPhysics.Movement movement) {
        return new WorldPos(
                (movement == AOPhysics.Movement.RIGHT ? 1 : movement == AOPhysics.Movement.LEFT ? -1 : 0) + pos.x,
                (movement == AOPhysics.Movement.UP ? -1 : movement == AOPhysics.Movement.DOWN ? 1 : 0) + pos.y,
                pos.map);
    }

}
