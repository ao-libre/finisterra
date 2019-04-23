package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public class Charisma extends Component implements Serializable {
    private int value;

    public Charisma() {}

    public Charisma(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
