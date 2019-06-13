package movement;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import physics.AOPhysics;
import position.WorldPos;

import java.io.Serializable;

@PooledWeaver
public class Destination extends Component implements Serializable {

    public WorldPos worldPos;
    public AOPhysics.Movement dir;

    public Destination() {
    }

    public Destination(WorldPos pos, AOPhysics.Movement dir) {
        this.worldPos = pos;
        this.dir = dir;
    }

}
