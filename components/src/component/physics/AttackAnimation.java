package component.physics;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class AttackAnimation extends Component implements Serializable {

    public static float DEFAULT_ANIM_TIME = 0.5f;

    public float time = DEFAULT_ANIM_TIME;

    public AttackAnimation() {
    }

}
