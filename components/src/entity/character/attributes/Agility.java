package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public class Agility extends Component implements Serializable {
    private int value;

    public Agility() {
    }

    public Agility(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
