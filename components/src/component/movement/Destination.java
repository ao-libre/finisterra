package component.movement;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.position.WorldPos;

import java.io.Serializable;

@PooledWeaver
public class Destination extends Component implements Serializable {

    public WorldPos pos;
    public int dir;

    public Destination() {
    }

    public Destination(WorldPos pos, int dir) {
        this.pos = pos;
        this.dir = dir;
    }

}
