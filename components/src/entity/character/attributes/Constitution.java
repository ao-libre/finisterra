package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public class Constitution extends Component implements Serializable {
    private int value;

    public Constitution() {
    }

    public Constitution(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
