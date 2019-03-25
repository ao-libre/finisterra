package shared.map.model;

import com.badlogic.gdx.utils.Array;

/**
 * Utility class for de-serializing tileset configuration from file
 */
public class TilesetConfig {
    private String texturePath;
    private int tileWidth;
    private int tileHeight;
    private Array<Array<String>> terrainDefs;

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public void setTerrainDefs(Array<Array<String>> terrainDefs) {
        this.terrainDefs = terrainDefs;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public Array<Array<String>> getTerrainDefs() {
        return terrainDefs;
    }
}
