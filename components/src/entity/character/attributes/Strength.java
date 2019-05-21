package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public class Strength extends Component implements Serializable {
    private int baseValue;
    private int currentValue;

    public Strength() {
    }

    public Strength(int value) {
        this.baseValue = value;
        this.currentValue = value;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(int value) {
        this.baseValue = value;
    }
}
