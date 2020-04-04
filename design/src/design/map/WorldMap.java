package design.map;

import com.esotericsoftware.minlog.Log;
import design.map.MapsToWorld.Coord;
import shared.model.map.Tile;

import java.util.Comparator;
import java.util.HashMap;

public class WorldMap {

    private HashMap<Coord, Tile> tiles = new HashMap<>();

    public WorldMap() {
    }

    public void addTile(Coord pos, Tile tile) {
        if (tiles.containsKey(pos)) {
            Log.info("World already contains pos: " + pos);
        }
        tiles.put(pos, tile);
    }

    public HashMap<Coord, Tile> getTiles() {
        return tiles;
    }

    public Tile getTile(Coord pos) {
        return tiles.get(pos);
    }

    public shared.model.map.Map toMap() {
        Coord maxX = tiles.keySet().stream().max(Comparator.comparingInt(Coord::getX)).get();
        Coord minX = tiles.keySet().stream().min(Comparator.comparingInt(Coord::getX)).get();
        Coord maxY = tiles.keySet().stream().max(Comparator.comparingInt(Coord::getY)).get();
        Coord minY = tiles.keySet().stream().min(Comparator.comparingInt(Coord::getY)).get();

        Log.info(String.format("Translated to: minX: %d, maxX: %d, minY: %d, maxY: %d", minX.x, maxX.x, minY.y, maxY.y));

        shared.model.map.Map map = new shared.model.map.Map(maxX.x + Math.abs(minX.x) + 1, maxY.y + Math.abs(minY.y) + 1);

        tiles.forEach((coord, tile) -> {
            int x = Math.abs(minX.x) + coord.x;
            int y = Math.abs(minY.y) + coord.y;
            if (x < 0 || y < 0) {
                Log.info("out of bounds");
            }
            map.setTile(x, y, tile);
        });
        return map;
    }
}
