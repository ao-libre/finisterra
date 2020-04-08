package component.entity.npc;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.position.WorldPos;

import java.util.Objects;

@PooledWeaver
public class OriginPos extends Component {

    public int map;
    public int x;
    public int y;

    public OriginPos() {
        this.x = 0;
        this.y = 0;
        this.map = 0;
    }

    public OriginPos(int x, int y) {
        this.x = x;
        this.y = y;
        this.map = 1; // wrong
    }

    public OriginPos(int x, int y, int map) {
        this.x = x;
        this.y = y;
        this.map = map;
    }

    public OriginPos(OriginPos pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.map = pos.map;
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

    public WorldPos toWorldPos() {
        return new WorldPos(x, y, map);
    }

    @Override
    public String toString() {
        return "(map: " + map + " x: " + x + " y: " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OriginPos worldPos = (OriginPos) o;
        return map == worldPos.map &&
                x == worldPos.x &&
                y == worldPos.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map, x, y);
    }
}
