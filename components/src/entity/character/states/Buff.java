package entity.character.states;

import com.artemis.Component;

public class Buff extends Component {
    private Class<? extends Component> attribute;

    private float time;

    public Buff() {}


    public Class<? extends Component> getAttribute() {
        return attribute;
    }

    public void setAttribute(Class<? extends Component> attribute) {
        this.attribute = attribute;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
