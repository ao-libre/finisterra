package entity.character.attributes;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public abstract class Attribute extends Component implements Serializable {

    private int maxAttributeValue = 40;
    private int baseValue;
    private int currentValue;

    public Attribute() {

    }

    public Attribute(int value) {
        this.baseValue = Math.min(value, maxAttributeValue);
        this.currentValue = baseValue;
    }

    public void resetCurrentValue(){
        currentValue = baseValue;
    }

    public void setMaxAttributeValue(int maxAttributeValue) { this.maxAttributeValue = maxAttributeValue; }
    public int getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(int value) {
        this.baseValue = Math.min(value, maxAttributeValue);
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int value) {
        this.currentValue = Math.min(value, maxAttributeValue);
    }
}
