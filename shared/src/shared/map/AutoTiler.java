package shared.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import javafx.util.Pair;
import shared.map.model.MapDescriptor;
import shared.map.model.MapDescriptor.MapDescriptorBuilder;
import shared.map.model.TerrainType;
import shared.map.model.TilesetConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * AutoTiler
 *
 * Procedurally generate a terrain map using corner matching "Wang Tiles"
 *  See: http://www.cr31.co.uk/stagecast/wang/2corn.html
 */
public class AutoTiler {

    public static MapDescriptor load(int mapWidth, int mapHeight, final FileHandle tilesetConfigFile) {
        final TilesetConfig conf = loadTileSetConfig(tilesetConfigFile);
        Pair<Integer, Integer> tileDimension = loadTileDimension(conf);
        Pair<Map<Byte, TerrainType>, List<List<Byte>>> terrainDefs = loadTerrainDefinitions(conf);

        return MapDescriptorBuilder //
                .create() //
                .withSize(mapWidth, mapHeight) //
                .withTexturePath(loadTexturePath(conf)) //
                .withTileDescriptor(tileDimension.getKey(), tileDimension.getValue()) //
                .withTerrainTypes(terrainDefs.getKey()) //
                .withTileRowTerrains(terrainDefs.getValue()) //
                .build();
    }

    private static Pair<Integer, Integer> loadTileDimension(TilesetConfig conf) {
        // Validate tile dimensions
        int tileWidth = conf.getTileWidth();
        if (tileWidth <= 0 || tileWidth > 128) {
            throw new IllegalArgumentException("Invalid tile width");
        }
        int tileHeight = conf.getTileHeight();
        if (tileHeight <= 0 || tileHeight > 128) {
            throw new IllegalArgumentException("Invalid tile height");
        }

        return new Pair(tileWidth, tileHeight);
    }

    private static String loadTexturePath(TilesetConfig conf) {
        // Validate texture path
        final FileHandle tilesTextureHandle = Gdx.files.internal(conf.getTexturePath());
        if (!tilesTextureHandle.exists() || tilesTextureHandle.isDirectory()) {
            throw new IllegalArgumentException("Invalid Tile-set texture path");
        }
        return conf.getTexturePath();
    }

    private static TilesetConfig loadTileSetConfig(FileHandle tilesetConfigFile) {
        // Load config
        final Json json = new Json();
        return json.fromJson(TilesetConfig.class, tilesetConfigFile);
    }

    /**
     * Load terrain definitions from tileset config,
     * Pre-compute some look-up tables
     *
     * @param config The loaded tileset configuration object
     */
    private static Pair<Map<Byte, TerrainType>, List<List<Byte>>> loadTerrainDefinitions(TilesetConfig config) {
        final Array<Array<String>> terrainDefs = config.getTerrainDefs();
        final Map<String, Byte> nameToIdMap = new HashMap<>();
        Map<Byte, TerrainType> terrainTypes = new HashMap<>();
        List<List<Byte>> tileRowTerrains = new ArrayList<>();

        byte currentTerrainId = 0;
        for (final Array<String> terrainDefsRow : terrainDefs) {
            if (terrainDefsRow.size != MapDescriptor.TERRAINS_PER_ROW) {
                throw new IllegalArgumentException(
                        "Each terrain_defs row must contain exactly " + MapDescriptor.TERRAINS_PER_ROW + " terrain types");
            }

            final List<Byte> terrainRow = new ArrayList<>(MapDescriptor.TERRAINS_PER_ROW);

            // Generate an Id for each terrain type
            for (final String terrainName : terrainDefsRow) {
                Byte id = nameToIdMap.get(terrainName);
                if (id == null) {
                    // Create new terrain type entity
                    id = currentTerrainId++;
                    nameToIdMap.put(terrainName, id);
                    terrainTypes.put(id, new TerrainType(id));
                }

                // Add terrain Id to row
                terrainRow.add(id);
            }

            // Add row to terrains configuration
            tileRowTerrains.add(terrainRow);

            // Mark transition between the above terrain types as valid
            final byte firstTerrainId = terrainRow.get(0);
            final byte secondTerrainId = terrainRow.get(1);
            terrainTypes.get(firstTerrainId).getTransitions().add(secondTerrainId);
            terrainTypes.get(secondTerrainId).getTransitions().add(firstTerrainId);
        }
        return new Pair(terrainTypes, tileRowTerrains);
    }

}