package physics;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.io.Serializable;

@DelayedComponentRemoval
public class Attack extends Component implements Serializable {

    public static float DEFAULT_INTERVAL = 1.2f;

    public float interval = DEFAULT_INTERVAL;

    public Attack() {
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public float getInterval() {
        return interval;
    }
}
