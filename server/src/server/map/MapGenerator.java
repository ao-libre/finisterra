package server.map;

import shared.map.model.MapDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import static shared.map.model.TILE_BITS.*;

public class MapGenerator {

    private static final byte MATCH_ANY = 127;

    /**
     * Procedurally generate a new terrain map
     *
     * @return The generated TileMap
     */
    public static int[][] generateMap(MapDescriptor descriptor) {
        int mapWidth = descriptor.getMapWidth();
        int mapHeight = descriptor.getMapHeight();
        int[][] result = new int[mapWidth][mapHeight];
        // Iterate on map cells from bottom-left to top-right
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                // Pick next tile
                result[col][row] = pickTile(descriptor, result, col, row);
            }
        }
        return result;
    }

    /**
     * Pick a tile for a certain map cell, based on its neighboring tiles
     *
     * @param descriptor map description
     * @param map        map tile ids
     * @param col        Map column
     * @param row        Map row
     * @return The ID of the picked tile in the tileset
     */
    private static int pickTile(MapDescriptor descriptor, int[][] map, final int col, final int row) {
        // Init all match mask elements to "dont-care"
        final byte[] matchMask = new byte[]{MATCH_ANY, MATCH_ANY, MATCH_ANY, MATCH_ANY};

        // Update match mask according to left tile corners
        updateMatchMaskForTile(descriptor,
                map,
                matchMask,
                col - 1, row,
                TOP_LEFT.id(), TOP_RIGHT.id(),
                BOTTOM_LEFT.id(), BOTTOM_RIGHT.id());

        // Update match mask according to bottom tile corners
        updateMatchMaskForTile(descriptor,
                map,
                matchMask,
                col, row - 1,
                BOTTOM_LEFT.id(), TOP_LEFT.id(),
                BOTTOM_RIGHT.id(), TOP_RIGHT.id());

        // Handle "special case" for terrain types without transition tiles
        final int tileId = getTileId(descriptor, map, col + 1, row - 1);
        if (tileId >= 0) {
            final byte tileCorner = getTerrainCodes(descriptor, tileId)[TOP_RIGHT.id()];
            final byte maskCorner = matchMask[TOP_LEFT.id()];
            if (maskCorner != tileCorner) {
                final TreeSet<Byte> validTransitions = (TreeSet<Byte>) descriptor.getTerrainTypes().get(tileCorner).getTransitions();
                if (validTransitions.size() < descriptor.getMaxTransitions()) {
                    matchMask[TOP_RIGHT.id()] = validTransitions.first();
                }
            }
        }

        // Find all tiles that match
        final List<Integer> matchingTiles = findMatchingTiles(descriptor, matchMask);

        // Pick one of the matching tiles
        final int selectedTile = ThreadLocalRandom.current().nextInt(matchingTiles.size());

        return matchingTiles.get(selectedTile);
    }

    /**
     * Update the corner matching tiles based on the corners of a neighboring tile
     *
     * @param descriptor   map descriptor
     * @param map          map tile ids
     * @param mask         The match mask [IN/OUT]
     * @param col          Column of the tile to match
     * @param row          Row of the tile to match
     * @param mask_corner0 1st corner to match from the current tile
     * @param tile_corner0 1st corner to match from the neighboring tile
     * @param mask_corner1 2nd corner to match from the current tile
     * @param tile_corner1 2nd corner to match from the neighboring tile
     */
    private static void updateMatchMaskForTile(MapDescriptor descriptor, int[][] map, final byte[] mask,
                                               final int col, final int row,
                                               final int mask_corner0, final int tile_corner0,
                                               final int mask_corner1, final int tile_corner1) {
        // Get tile Id at position
        final int tileId = getTileId(descriptor, map, col, row);

        if (tileId >= 0) {
            // Extract tile bit codes
            final byte[] tileCodes = getTerrainCodes(descriptor, tileId);

            // Update match mask
            mask[mask_corner0] = tileCodes[tile_corner0];
            mask[mask_corner1] = tileCodes[tile_corner1];
        }
    }

    /**
     * Get the tile ID at a certain map cell
     *
     * @param descriptor map descriptor
     * @param map        map tile ids
     * @param col        Column
     * @param row        Row
     * @return The tile ID at map cell (col, row)
     */
    private static int getTileId(MapDescriptor descriptor, int[][] map, final int col, final int row) {
        if (col < 0 || row < 0 || col >= descriptor.getMapWidth() || row >= descriptor.getMapHeight()) {
            return -1;
        }

        return map[col][row];
    }

    /**
     * Find all tiles in our tileset which match the constraints of the match mask
     *
     * @param descriptor map descriptor
     * @param mask       Match mask
     * @return A list of matching tile IDs
     */
    private static List<Integer> findMatchingTiles(MapDescriptor descriptor, final byte[] mask) {
        final List<Integer> matchingTiles = new ArrayList<>();

        final int maskLength = mask.length;
        final int numTiles = descriptor.getTilesPerTerrain() * descriptor.getTerraninsPerRow();
        for (int i = 0; i < numTiles; i++) {
            final byte[] bits = getTerrainCodes(descriptor, i);
            int j = 0;
            for (; j < maskLength; j++) {
                if (mask[j] != MATCH_ANY && mask[j] != bits[j]) {
                    break;
                }
            }
            if (j == maskLength) {
                matchingTiles.add(i);
            }
        }

        return matchingTiles;
    }

    /**
     * Extract the terrain type codes from a specific tile ID
     *
     * @param descriptor map descriptor
     * @param tileId     Tile ID
     * @return An array of terrain codes for each tile corner
     */
    private static byte[] getTerrainCodes(MapDescriptor descriptor, final int tileId) {
        byte[] values = new byte[]{
                (byte) (tileId & 0x1),
                (byte) ((tileId & 0x2) >> 1),
                (byte) ((tileId & 0x4) >> 2),
                (byte) ((tileId & 0x8) >> 3)
        };

        // Transform Terrain Id according to terrain defs
        final int tilesRowIndex = tileId / descriptor.getTilesPerTerrain();
        final List<Byte> terrainRow = descriptor.getTileRowTerrains().get(tilesRowIndex);
        for (int i = 0; i < values.length; i++) {
            values[i] = terrainRow.get(values[i]);
        }

        return values;
    }
}
