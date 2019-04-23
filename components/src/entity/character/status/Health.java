package entity.character.status;

import com.artemis.Component;

import java.io.Serializable;

public class Health extends Component implements Serializable {

    public int min;
    public int max;

    public Health() {
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
