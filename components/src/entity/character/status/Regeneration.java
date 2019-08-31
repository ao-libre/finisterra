package entity.character.status;

import com.artemis.Component;

import java.io.Serializable;

public class Regeneration extends Component implements Serializable {

    public static final float DEFAULT = 2f;

    private float multiplier;

    public Regeneration() {}
    public Regeneration(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}
