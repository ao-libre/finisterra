package shared.model.map;

public class Map {

    public static final int MAX_MAP_SIZE_WIDTH = 100;
    public static final int MIN_MAP_SIZE_WIDTH = 1;
    public static final int MAX_MAP_SIZE_HEIGHT = 100;
    public static final int MIN_MAP_SIZE_HEIGHT = 1;

    public static final int TILE_BUFFER_SIZE = 7;
    protected Tile tiles[][];
    private boolean secureZone;

    public Map() {
        this.tiles = new Tile[MAX_MAP_SIZE_WIDTH + 1][MAX_MAP_SIZE_HEIGHT + 1];
    }

    public Map(int width, int height) {
        this.tiles = new Tile[width][height];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Tile getTile(int x, int y) {
        return this.tiles[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        this.tiles[x][y] = tile;
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public boolean isSecureZone() {
        return secureZone;
    }

    public void setSecureZone(boolean secureZone) {
        this.secureZone = secureZone;
    }
}