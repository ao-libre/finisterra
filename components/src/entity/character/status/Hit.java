package entity.character.status;

import com.artemis.Component;

import java.io.Serializable;

public class Hit extends Component implements Serializable {

    private int min;
    private int max;

    public Hit() {}

    public Hit(int min, int max) {

        this.min = min;
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
