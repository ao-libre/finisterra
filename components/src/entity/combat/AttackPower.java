package entity.combat;

import com.artemis.Component;

import java.io.Serializable;

public class AttackPower extends Component implements Serializable {

    public int value;
    public AttackPower() {}

    public AttackPower(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
