package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public class Strength extends Component implements Serializable {
    private int value;

    public Strength() {
    }

    public Strength(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
