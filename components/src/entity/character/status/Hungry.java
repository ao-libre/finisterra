package entity.character.status;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Hungry extends Component implements Serializable, Stat {

    public int min;
    public int max;

    public Hungry() {
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
