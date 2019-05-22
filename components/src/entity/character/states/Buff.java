package entity.character.states;

import com.artemis.Component;
import entity.character.attributes.Attribute;

public class Buff extends Component {
    private Attribute attribute;

    private float time;

    public Buff() {}

    public Buff(Attribute attrib, float timeDuration)
    {
        attribute = attrib;
        time = timeDuration;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
