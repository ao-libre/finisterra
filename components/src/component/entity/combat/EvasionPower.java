package component.entity.combat;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class EvasionPower extends Component implements Serializable {

    public int value;

    public EvasionPower() {
    }

    public EvasionPower(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
