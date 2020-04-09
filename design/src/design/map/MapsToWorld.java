package design.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.esotericsoftware.minlog.Log;
import shared.model.map.Map;
import shared.model.map.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MapsToWorld {

    private static Set<Integer> excluded = new HashSet<>();

    public static void createWorld() {
        excluded.add(237);
        excluded.add(162);
        // load maps
        HashMap<Integer, Map> maps = new HashMap<>();
//        MapHelper.instance().getAlkonMaps(maps);
        WorldMap world = new WorldMap();
        // visit maps
        visitMap(maps, 1, 0, 0, world, new HashSet<>());
        Map map = world.toMap();
        System.out.println("World tiles count: " + world.getTiles().size());
        save(map);
    }

    private static void save(Map map) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.toJson(map, Gdx.files.local("/output/1.json"));
    }

    private static void visitMap(HashMap<Integer, Map> maps, int i, int offsetX, int offsetY, WorldMap world, Set<Integer> visited) {
        if (!maps.containsKey(i) || excluded.contains(i)) {
            return;
        }
        if (visited.contains(i)) {
            return;
        }
        Map map = maps.get(i);
        // desde cual empiezo?
        Corners corners = new Corners();
        if (!corners.allSet()) {
            corners.topRight = Optional.ofNullable(corners.topRight).orElse(new Coord(90, 10));
            corners.topLeft = Optional.ofNullable(corners.topLeft).orElse(new Coord(10, 10));
            corners.bottomLeft = Optional.ofNullable(corners.bottomLeft).orElse(new Coord(10, 90));
            corners.bottomRight = Optional.ofNullable(corners.bottomRight).orElse(new Coord(90, 90));
        }
        Log.info("Map visited: " + i);
        fillWorld(map, corners, world, offsetX, offsetY);

        int effectiveWidth = corners.bottomRight.x - corners.bottomLeft.x;
        int effectiveHeight = corners.bottomLeft.y - corners.topLeft.y;
        // convertir en relativo a pos

        visited.add(i);
        Log.info("From " + i + " Move up");
        visitMap(maps, getMap(Dir.UP, map), offsetX, offsetY - effectiveHeight, world, visited);
        Log.info("From " + i + " Move down");
        visitMap(maps, getMap(Dir.DOWN, map), offsetX, offsetY + effectiveHeight, world, visited);
        Log.info("From " + i + " Move left");
        visitMap(maps, getMap(Dir.LEFT, map), offsetX - effectiveWidth, offsetY, world, visited);
        Log.info("From " + i + " Move right");
        visitMap(maps, getMap(Dir.RIGHT, map), offsetX + effectiveWidth, offsetY, world, visited);
    }

    private static int getMap(Dir dir, Map map) {
        for (int x = 1; x < Map.MAX_MAP_SIZE_WIDTH; x++) {
            for (int y = 1; y < Map.MAX_MAP_SIZE_HEIGHT; y++) {
                Tile tile = map.getTile(x, y);
                if (tile.getTileExit() != null) {
                    switch (dir) {
                        case DOWN:
                            if (y > Map.MAX_MAP_SIZE_HEIGHT / 2) {
                                Optional<Tile> right = getTileWithExit(Dir.RIGHT, x, y, map);
                                Optional<Tile> left = getTileWithExit(Dir.LEFT, x, y, map);
                                if (right.isPresent() || left.isPresent()) {
                                    return tile.getTileExit().getMap();
                                }
                            }
                            break;
                        case RIGHT:
                            if (x > Map.MAX_MAP_SIZE_WIDTH / 2) {
                                Optional<Tile> up = getTileWithExit(Dir.UP, x, y, map);
                                Optional<Tile> down = getTileWithExit(Dir.DOWN, x, y, map);
                                if (up.isPresent() || down.isPresent()) {
                                    return tile.getTileExit().getMap();
                                }
                            }
                            break;
                        case LEFT:
                            if (x < Map.MAX_MAP_SIZE_WIDTH / 2) {
                                Optional<Tile> up = getTileWithExit(Dir.UP, x, y, map);
                                Optional<Tile> down = getTileWithExit(Dir.DOWN, x, y, map);
                                if (up.isPresent() || down.isPresent()) {
                                    return tile.getTileExit().getMap();
                                }
                            }
                            break;
                        case UP:
                            if (y < Map.MAX_MAP_SIZE_HEIGHT / 2) {
                                Optional<Tile> right = getTileWithExit(Dir.RIGHT, x, y, map);
                                Optional<Tile> left = getTileWithExit(Dir.LEFT, x, y, map);
                                if (right.isPresent() || left.isPresent()) {
                                    return tile.getTileExit().getMap();
                                }
                            }
                            break;
                    }
                }
            }
        }
        return -1;
    }

    private static void fillWorld(Map map, Corners corners, WorldMap world, int offsetX, int offsetY) {
        Log.info(String.format("Fill world with map offsetX: %d - offsetY: %d", offsetX, offsetY));
        for (int x = corners.bottomLeft.x; x < corners.bottomRight.x; x++) {
            for (int y = corners.topLeft.y; y < corners.bottomLeft.y; y++) {
                Tile tile = map.getTile(x, y);
                world.addTile(new Coord(x + offsetX, y + offsetY), tile);
            }
        }
    }

    private static Corners getCorners(Map map) {
        Corners corners = new Corners();
        for (int x = 1; x <= Map.MAX_MAP_SIZE_WIDTH; x++) {
            for (int y = 1; y <= Map.MAX_MAP_SIZE_HEIGHT; y++) {
                Tile tile = map.getTile(x, y);
                if (tile == null || tile.isBlocked() || tile.getTileExit() != null) {
                    continue;
                }
                Optional<Tile> up = getTileWithExit(Dir.UP, x, y, map);
                Optional<Tile> down = getTileWithExit(Dir.DOWN, x, y, map);
                Optional<Tile> left = getTileWithExit(Dir.LEFT, x, y, map);
                Optional<Tile> right = getTileWithExit(Dir.RIGHT, x, y, map);
                if (up.isPresent()) {
                    if (left.isPresent()) {
                        if (!down.isPresent() && !right.isPresent()) {
                            corners.topLeft = new Coord(x, y);
                        }
                    } else if (right.isPresent()) {
                        if (!down.isPresent()) {
                            corners.topRight = new Coord(x, y);
                        }
                    }
                } else if (down.isPresent()) {
                    if (left.isPresent()) {
                        if (!right.isPresent()) {
                            corners.bottomLeft = new Coord(x, y);
                        }
                    } else if (right.isPresent()) {
                        corners.bottomRight = new Coord(x, y);
                    }
                }
            }
        }
        return corners;
    }

    private static Optional<Tile> getTileWithExit(Dir dir, int x, int y, Map map) {
        int x2 = dir.equals(Dir.LEFT) ? x - 1 : dir.equals(Dir.RIGHT) ? x + 1 : x;
        int y2 = dir.equals(Dir.UP) ? y - 1 : dir.equals(Dir.DOWN) ? y + 1 : y;
        if (x2 < 0 || y2 < 0 || x2 >= Map.MAX_MAP_SIZE_WIDTH || y2 >= Map.MAX_MAP_SIZE_HEIGHT) {
            return Optional.empty();
        }
        Tile tile = map.getTile(x2, y2);
        return tile != null && tile.getTileExit() != null ? Optional.ofNullable(tile) : Optional.empty();
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

        public boolean allSet() {
            return topLeft != null && bottomRight != null && bottomLeft != null && topRight != null;
        }
    }

    static final class Coord {
        int x;
        int y;

        Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("X: %d - Y: %d", x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

}
