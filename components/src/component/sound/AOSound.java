package component.sound;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class AOSound extends Component {

    public int id = -1;
    public boolean shouldLoop = false;

    public AOSound() {
    }

    public AOSound(int ID, boolean loop) {
        setId(ID);
        setShouldLoop(loop);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShouldLoop(boolean shouldLoop) {
        this.shouldLoop = shouldLoop;
    }

}
