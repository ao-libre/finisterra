package component.physics;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class AttackInterval extends Component implements Serializable {

    private float value;

    public AttackInterval() {
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
