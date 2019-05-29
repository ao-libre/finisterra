package map;

import game.managers.WorldManager;
import position.Pos2D;
import position.WorldPos;
import shared.model.map.Tile;

import java.util.HashMap;

public class WorldMap {

    private HashMap<Pos2D, Tile> tiles = new HashMap<>();

    public WorldMap(){}

    public void addTile(Pos2D pos, Tile tile) {
        tiles.put(pos, tile);
    }

    public HashMap<Pos2D, Tile> getTiles() {
        return tiles;
    }

    public Tile getTile(Pos2D pos) {
        return tiles.get(pos);
    }
}
