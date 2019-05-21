package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public abstract class Attribute extends Component implements Serializable {
    private int baseValue;
    private int currentValue;

    public Attribute() {

    }

    public Attribute(int value) {
        this.baseValue = value;
        this.currentValue = value;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(int value) {
        this.baseValue = value;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }
}
