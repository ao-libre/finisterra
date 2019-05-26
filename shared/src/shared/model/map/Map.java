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

    public Tile getTile(int x, int y) {
        return this.tiles[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        this.tiles[x][y] = tile;
    }

    public boolean isSecureZone() {
        return secureZone;
    }

    public void setSecureZone(boolean secureZone) {
        this.secureZone = secureZone;
    }
}
