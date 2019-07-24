package shared.model.map;

import java.util.Objects;

public class WorldPosition {

    private int map;
    private int x;
    private int y;

    public WorldPosition() {
        this(0, 0, 0);
    }

    /**
     * @param map
     * @param x
     * @param y
     */
    public WorldPosition(int map, int x, int y) {
        this.map = map;
        this.setX(x);
        this.setY(y);
    }

    /**
     * @return the map
     */
    public int getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(int map) {
        this.map = map;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("[Map: %d - X: %d - Y: %d", map, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldPosition that = (WorldPosition) o;
        return map == that.map &&
                x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map, x, y);
    }
}
