package position;

import com.artemis.Component;
import entity.character.states.Heading;

import java.util.Objects;

import static entity.character.states.Heading.*;

public class WorldPos extends Component {

    public int map;
    public int x;
    public int y;
    public float offsetX;
    public float offsetY;

    public WorldPos() {
        this.x = 0;
        this.y = 0;
        this.map = 0;
    }

    public WorldPos(int x, int y) {
        this.x = x;
        this.y = y;
        this.map = 1; // wrong
    }

    public WorldPos(int x, int y, int map) {
        this.x = x;
        this.y = y;
        this.map = map;
    }

    public WorldPos(WorldPos pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.map = pos.map;
    }

    public WorldPos getNextPos(Heading facing) {
        switch (facing.current) {
            case HEADING_NORTH:
                return new WorldPos(x, y - 1, map);
            case HEADING_SOUTH:
                return new WorldPos(x, y + 1, map);
            case HEADING_EAST:
                return new WorldPos(x + 1, y, map);
            case HEADING_WEST:
                return new WorldPos(x - 1, y, map);
        }
        return new WorldPos();
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Pos2D getPos2D() {
        return new Pos2D(x + offsetX, y + offsetY);
    }

    @Override
    public String toString() {
        return "(map: " + map + " x: " + x + " y: " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldPos worldPos = (WorldPos) o;
        return map == worldPos.map &&
                x == worldPos.x &&
                y == worldPos.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map, x, y);
    }
}
