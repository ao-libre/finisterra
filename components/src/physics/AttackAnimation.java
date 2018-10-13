package physics;

import com.artemis.Component;

import java.io.Serializable;

public class AttackAnimation extends Component implements Serializable {

    public static float DEFAULT_ANIM_TIME = 0.5f;

    public float time = DEFAULT_ANIM_TIME;

    public AttackAnimation() {}

}
