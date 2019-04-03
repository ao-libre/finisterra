package shared.map.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import map.Cave;

public class CaveToMap {

    private String texturePath = "map/tileset.png";
    private MapDescriptor.TileDescriptor tileDescriptor = new MapDescriptor.TileDescriptor(32, 32);

    private int mapWidth;
    private int mapHeight;
    private boolean[][] tiles;

    public CaveToMap(Cave cave) {
        this.mapHeight = cave.height;
        this.mapWidth = cave.width;
        this.tiles = cave.tiles;
    }

    public TiledMap create() {
        Texture tilesTexture = new Texture(texturePath);
        TiledMapTileSet tileSet = getTileSet(tilesTexture);
        TiledMap map = new TiledMap();
        TiledMapTileLayer mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, tileDescriptor.getTileWidth(), tileDescriptor.getTileHeight());
        map.getLayers().add(mapLayer);
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                Cell cell = new Cell();
                boolean[] neighbours = getNeighbours(tiles, x, y);
                cell.setTile(tileSet.getTile(tiles[x][y] ? getWall(neighbours) : getTile(neighbours)));
                cell.setFlipVertically(true);
                mapLayer.setCell(x, y, cell);
            }
        }
        return map;
    }

    private boolean[] getNeighbours(boolean[][] tiles, int x, int y) {
        boolean[] neighbours = new boolean[8];
        neighbours[Position.UP_LEFT.ordinal()] = (x <= 0) || (y <= 0) || tiles[x - 1][y - 1];
        neighbours[Position.UP.ordinal()] = (y <= 0) || tiles[x][y - 1];
        neighbours[Position.UP_RIGHT.ordinal()] = (x >= mapWidth - 1) || (y <= 0) || tiles[x][y - 1];
        neighbours[Position.LEFT.ordinal()] = (x <= 0) || tiles[x - 1][y];
        neighbours[Position.RIGHT.ordinal()] = (x >= mapWidth - 1) || tiles[x + 1][y];
        neighbours[Position.DOWN_LEFT.ordinal()] = (x <= 0) || (y >= mapHeight - 1) || tiles[x][y + 1];
        neighbours[Position.DOWN.ordinal()] = (y >= mapHeight - 1) || tiles[x][y + 1];
        neighbours[Position.DOWN_RIGHT.ordinal()] = (x >= mapWidth - 1) || (y >= mapHeight - 1) || tiles[x][y + 1];
        return neighbours;
    }

    private int getTile(boolean[] neighbours) {
        boolean up = !neighbours[Position.UP.ordinal()];
        boolean upleft = !neighbours[Position.UP_LEFT.ordinal()];
        boolean upright = !neighbours[Position.UP_RIGHT.ordinal()];
        boolean down = !neighbours[Position.DOWN.ordinal()];
        boolean downleft = !neighbours[Position.DOWN_LEFT.ordinal()];
        boolean downright = !neighbours[Position.DOWN_RIGHT.ordinal()];
        boolean left = !neighbours[Position.LEFT.ordinal()];
        boolean right = !neighbours[Position.RIGHT.ordinal()];
        Tiles result = Tiles.EMPTY;

        if (right && down && !left && !up) result = Tiles.GROUND_UP_LEFT;
        if (right && down && left && !up) result = Tiles.GROUND_UP;
        if (!right && down && left && !up) result = Tiles.GROUND_UP_RIGHT;
        if (right && down && !left && up) result = Tiles.GROUND_CENTER_LEFT;
        if (right && down && left && up) result = Tiles.GROUND_CENTER;
        if (!right && down && left && up) result = Tiles.GROUND_CENTER_RIGHT;
        if (right && !down && !left && up) result = Tiles.GROUND_DOWN_LEFT;
        if (right && !down && left && up) {
            result = Tiles.GROUND_DOWN;
        }
        if (!right && !down && left && up) result = Tiles.GROUND_DOWN_RIGHT;

        return result.ordinal();
    }

    private int getWall(boolean[] neighbours) {
        Tiles result = Tiles.EMPTY;
        boolean up = !neighbours[Position.UP.ordinal()];
        boolean upleft = !neighbours[Position.UP_LEFT.ordinal()];
        boolean upright = !neighbours[Position.UP_RIGHT.ordinal()];
        boolean down = !neighbours[Position.DOWN.ordinal()];
        boolean downleft = !neighbours[Position.DOWN_LEFT.ordinal()];
        boolean downright = !neighbours[Position.DOWN_RIGHT.ordinal()];
        boolean left = !neighbours[Position.LEFT.ordinal()];
        boolean right = !neighbours[Position.RIGHT.ordinal()];
//        if (downleft && !down && !left) result = Tiles.WALL_LEFT_UP;
//        if (down) result = Tiles.WALL_UP;
//        if (downright && !down && !right) result = Tiles.WALL_RIGHT_UP;
//        if (left) result = Tiles.WALL_RIGHT;
//        if (right) result = Tiles.WALL_LEFT;
//        if (upleft && !up && !left) result = Tiles.WALL_LEFT_DOWN;
//        if (up) result = Tiles.WALL_DOWN;
//        if (upright && !up && !right) result = Tiles.WALL_RIGHT_DOWN;
        return result.ordinal();
    }

    private TiledMapTileSet getTileSet(Texture tilesTexture) {
        // Split into tiles
        final TextureRegion[][] splitTiles = TextureRegion.split(tilesTexture, tileDescriptor.getTileWidth(), tileDescriptor.getTileHeight());
        TiledMapTileSet tileSet = new TiledMapTileSet();
        int tid = 0;
        for (TextureRegion[] splitTile : splitTiles) {
            for (TextureRegion aSplitTile : splitTile) {
                final StaticTiledMapTile tile = new StaticTiledMapTile(aSplitTile);
                tile.setId(tid++);
                tileSet.putTile(tile.getId(), tile);
            }
        }
        return tileSet;
    }

    public enum Position {
        UP_LEFT,
        UP,
        UP_RIGHT,
        LEFT,
        RIGHT,
        DOWN_LEFT,
        DOWN,
        DOWN_RIGHT
    }

    public enum Tiles {
        GROUND_UP_LEFT,
        GROUND_UP,
        GROUND_UP_RIGHT,
        WALL_LEFT_UP,
        WALL_UP,
        WALL_RIGHT_UP,
        GROUND_CENTER_LEFT,
        GROUND_CENTER,
        GROUND_CENTER_RIGHT,
        WALL_LEFT,
        EMPTY,
        WALL_RIGHT,
        GROUND_DOWN_LEFT,
        GROUND_DOWN,
        GROUND_DOWN_RIGHT,
        WALL_LEFT_DOWN,
        WALL_DOWN,
        WALL_RIGHT_DOWN
    }

}
