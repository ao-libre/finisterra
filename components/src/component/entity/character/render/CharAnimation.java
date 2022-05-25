package component.entity.character.render;

import com.artemis.Component;

public class CharAnimation extends Component {
    private float time;
    private float duration;

    public CharAnimation() {}

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void add(float deltaTime) {
        time += deltaTime;
        if (time > duration) {
            time -= duration;
        }
    }
}
