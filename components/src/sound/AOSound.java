package sound;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class AOSound extends Component {

    public int soundID = -1;
    public boolean shouldLoop = false;

    public AOSound() {
    }

    public AOSound(int ID, boolean loop) {
        setSoundID(ID);
        setShouldLoop(loop);
    }

    public void setSoundID(int soundID) {
        this.soundID = soundID;
    }

    public void setShouldLoop(boolean shouldLoop) {
        this.shouldLoop = shouldLoop;
    }

}
