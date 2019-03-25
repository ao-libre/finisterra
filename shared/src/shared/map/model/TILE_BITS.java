package shared.map.model;

// Tile corners encoding
public enum TILE_BITS {
    TOP_LEFT(0), TOP_RIGHT(1), BOTTOM_LEFT(2), BOTTOM_RIGHT(3);

    private final int value;

    TILE_BITS(int value) {
        this.value = value;
    }

    public int id() {
        return value;
    }
}