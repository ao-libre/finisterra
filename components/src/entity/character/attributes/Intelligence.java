package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public class Intelligence extends Component implements Serializable {
    private int value;

    public Intelligence() {
    }

    public Intelligence(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
