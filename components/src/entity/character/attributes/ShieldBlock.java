package entity.character.attributes;

import com.artemis.Component;

public class ShieldBlock extends Component {

    private float probability; // 0 to 1

    public ShieldBlock() {}

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }
}
