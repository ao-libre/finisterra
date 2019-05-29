package map;

import game.handlers.MapHandler;
import position.Pos2D;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MapsToWorld {

    public static void main(String[] args) {
        // load maps
        HashMap<Integer, Map> maps = new HashMap<>();
        MapHandler.getHelper().initializeMaps(maps);

        Map world = new Map();
        // visit maps
        visitMap(maps, 1, 0, 0, world, new HashSet<>());
    }

    private static void visitMap(HashMap<Integer, Map> maps, int i, int offsetX, int offsetY, Map world, Set<Integer> visited) {
        if (!maps.containsKey(i)) {
            return;
        }
        if (visited.contains(i)) {
            return;
        }
        visited.add(i);
        Map map = maps.get(i);
        // desde cual empiezo?
        Corners corners = getCorners(map);
        fillWorld(map, corners, world, offsetX, offsetY);

        int effectiveWidth = corners.bottomRight.x - corners.bottomLeft.x;
        int effectiveHeight = corners.topLeft.y - corners.bottomLeft.y;
        // convertir en relativo a pos

        visitMap(maps, getMap(Dir.UP, map), offsetX, offsetY + effectiveHeight, world, visited);
        visitMap(maps, getMap(Dir.DOWN, map), offsetX, offsetY - effectiveHeight, world, visited);
        //
    }

    private static int getMap(Dir up, Map map) {
    }

    private static void fillWorld(Map map, Corners corners, Map world, int offsetX, int offsetY) {
    }

    private static Corners getCorners(Map map) {
    }

    enum Dir {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    static final class Corners {
        private Coord topLeft;
        private Coord bottomLeft;
        private Coord topRight;
        private Coord bottomRight;

        private Corners() {}

        private static

    }

    static final class Coord {
        int x;
        int y;

        public Coord(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

}
