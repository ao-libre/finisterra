package component.entity.character.attributes;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public abstract class Attribute extends Component implements Serializable {

    public static final int MAX_ATTRIBUTE_VALUE = 40;
    private int baseValue;
    private int currentValue;

    public Attribute() {

    }

    public Attribute(int value) {
        this.baseValue = Math.min(value, MAX_ATTRIBUTE_VALUE);
        this.currentValue = baseValue;
    }

    public void resetCurrentValue() {
        currentValue = baseValue;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(int value) {
        this.baseValue = Math.min(value, MAX_ATTRIBUTE_VALUE);
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int value) {
        this.currentValue = Math.min(value, MAX_ATTRIBUTE_VALUE);
    }
}
