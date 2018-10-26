package ar.com.tamborindeguy.util;

import ar.com.tamborindeguy.model.map.Tile;
import physics.AOPhysics;
import position.Pos2D;
import position.WorldPos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Util {

    public static short leShort(short n) {
        return (short) (((n & 0xff) << 8) | (((n & 0xff00) >> 8) & 0xff));
    }

    public static int leInt(int n) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putInt(n);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        return buf.getInt(0);
    }

    public static int leFloat(float n) {

        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putFloat(n);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        return buf.getInt(0);
    }

    public static Pos2D toScreen(WorldPos worldPos) {
        return new Pos2D(worldPos.x * Tile.TILE_PIXEL_WIDTH,
                worldPos.y * Tile.TILE_PIXEL_HEIGHT);
    }

    public static Pos2D toScreen(Pos2D pos) {
        return new Pos2D(pos.x * Tile.TILE_PIXEL_WIDTH,
                pos.y * Tile.TILE_PIXEL_HEIGHT);
    }

    public static Pos2D toGUI(Pos2D pos) {
        return new Pos2D(pos.x * Tile.TILE_PIXEL_WIDTH,
                pos.y * Tile.TILE_PIXEL_HEIGHT);
    }

    public static WorldPos toWorld(Pos2D pos) {
        return new WorldPos((int) (pos.x / Tile.TILE_PIXEL_WIDTH) + 1, (int) (pos.y / Tile.TILE_PIXEL_HEIGHT) + 1);
    }

    public static WorldPos getNextPos(WorldPos pos, AOPhysics.Movement movement) {
        return new WorldPos(
                (movement == AOPhysics.Movement.RIGHT ? 1 : movement == AOPhysics.Movement.LEFT ? -1 : 0) + pos.x,
                (movement == AOPhysics.Movement.UP ? -1 : movement == AOPhysics.Movement.DOWN ? 1 : 0) + pos.y,
                pos.map);
    }

}
