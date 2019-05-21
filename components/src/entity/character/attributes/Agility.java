package entity.character.attributes;

import com.artemis.Component;

import java.io.Serializable;

public class Agility extends Component implements Serializable {
    private int baseValue;
    private int currentValue;

    public Agility() {
    }

    public Agility(int value) {
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
