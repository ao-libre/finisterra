package physics;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class Attack extends Component implements Serializable {

    public static float DEFAULT_INTERVAL = 1.2f;

    public float interval = DEFAULT_INTERVAL;

    public Attack() {
    }

    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }
}
