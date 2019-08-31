package entity.character.status;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Thirst extends Component implements Serializable, Stat {

    public int min;
    public int max;

    public Thirst() {
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public void setMin(int min) {
        this.min = min;
    }
}
