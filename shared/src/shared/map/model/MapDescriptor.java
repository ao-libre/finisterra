package shared.map.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.List;
import java.util.Map;

public class MapDescriptor {

    // Each tile-set row should contain two main terrain tiles and 14 transition tiles
    // (4 bit encoded tile indices: 0 - 15)
    public static final int TERRAINS_PER_ROW = 2;
    public static final int TILES_PER_TERRAIN = 16;

    private final int terraninsPerRow = TERRAINS_PER_ROW;
    private final int tilesPerTerrain = TILES_PER_TERRAIN;
    private int mapWidth;
    private int mapHeight;
    private TileDescriptor tileDescriptor;
    private Map<Byte, TerrainType> terrainTypes;
    private List<List<Byte>> tileRowTerrains;
    private String texturePath;

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getTileWidth() {
        return tileDescriptor.getTileWidth();
    }

    public int getTileHeight() {
        return tileDescriptor.getTileHeight();
    }

    public Map<Byte, TerrainType> getTerrainTypes() {
        return terrainTypes;
    }

    public List<List<Byte>> getTileRowTerrains() {
        return tileRowTerrains;
    }

    public int getMaxTransitions() {
        return terrainTypes.size() - 1;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getTerraninsPerRow() {
        return terraninsPerRow;
    }

    public int getTilesPerTerrain() {
        return tilesPerTerrain;
    }

    public TiledMap create(int[][] tiles) {
        Texture tilesTexture = new Texture(getTexturePath());
        TiledMapTileSet tileSet = getTileSet(tilesTexture);
        // Create an empty map
        TiledMap map = new TiledMap();
        TiledMapTileLayer mapLayer = new TiledMapTileLayer(getMapWidth(), getMapHeight(), getTileWidth(), getTileHeight());
        map.getLayers().add(mapLayer);
        final Array<Texture> textures = Array.with(tilesTexture);
        map.setOwnedResources(textures);

        populate(tiles, tileSet, mapLayer);

        return map;
    }

    private void populate(int[][] tiles, TiledMapTileSet tileSet, TiledMapTileLayer mapLayer) {
        for (int row = 0; row < getMapHeight(); row++) {
            for (int col = 0; col < getMapWidth(); col++) {
                // Pick next tile
                final int tileId = tiles[col][row];
                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tileSet.getTile(tileId));
                cell.setFlipVertically(true);
                mapLayer.setCell(col, row, cell);
            }
        }
    }

    private TiledMapTileSet getTileSet(Texture tilesTexture) {
        // Split into tiles
        final TextureRegion[][] splitTiles = TextureRegion.split(tilesTexture, getTileWidth(), getTileHeight());
        final int numRows = splitTiles.length;
        if (numRows != getTerraninsPerRow()) {
            throw new IllegalArgumentException("Tileset rows do not match terrain definitions");
        }

        // Validate number of tiles per row
        for (final TextureRegion[] splitTile : splitTiles) {
            if (splitTile.length != getTilesPerTerrain()) {
                throw new IllegalArgumentException("Each tileset row must have exactly " + getTilesPerTerrain() + " tiles");
            }
        }

        // Create tileset
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

    public static class TileDescriptor {
        private final int tileWidth;
        private final int tileHeight;

        TileDescriptor(int tileWidth, int tileHeight) {
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
        }

        int getTileHeight() {
            return tileHeight;
        }

        int getTileWidth() {
            return tileWidth;
        }
    }

    public static class MapDescriptorBuilder {

        private static MapDescriptor mapDescriptor;

        public static MapDescriptorBuilder create() {
            mapDescriptor = new MapDescriptor();
            return new MapDescriptorBuilder();
        }

        public MapDescriptorBuilder withSize(int mapWidth, int mapHeight) {
            mapDescriptor.mapWidth = mapWidth;
            mapDescriptor.mapHeight = mapHeight;
            return this;
        }

        public MapDescriptorBuilder withTileDescriptor(int tileWidth, int tileHeight) {
            mapDescriptor.tileDescriptor = new TileDescriptor(tileWidth, tileHeight);
            return this;
        }

        public MapDescriptorBuilder withTerrainTypes(Map<Byte, TerrainType> terrainTypes) {
            mapDescriptor.terrainTypes = terrainTypes;
            return this;
        }

        public MapDescriptorBuilder withTileRowTerrains(List<List<Byte>> tileRowTerrains) {
            mapDescriptor.tileRowTerrains = tileRowTerrains;
            return this;
        }

        public MapDescriptorBuilder withTexturePath(String texturePath) {
            mapDescriptor.texturePath = texturePath;
            return this;
        }

        public MapDescriptor build() {
            return mapDescriptor;
        }
    }
}
