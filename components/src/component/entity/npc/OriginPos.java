package component.entity.npc;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.position.WorldPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@PooledWeaver
@Getter
@Setter
@AllArgsConstructor
public class OriginPos extends Component {

    public int map;
    public int x;
    public int y;

    public OriginPos() {
        this.x = 0;
        this.y = 0;
        this.map = 0;
    }

    public OriginPos(OriginPos pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.map = pos.map;
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
