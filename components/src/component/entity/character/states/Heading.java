package component.entity.character.states;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Heading extends Component implements Serializable {

    public static final int HEADING_NORTH = 0;
    public static final int HEADING_EAST = 1;
    public static final int HEADING_SOUTH = 2;
    public static final int HEADING_WEST = 3;

    public int current = HEADING_SOUTH;

    public Heading() {
    }

    public Heading(int heading) {
        this.current = heading;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
