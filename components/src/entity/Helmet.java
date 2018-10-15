package entity;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.io.Serializable;

@DelayedComponentRemoval
public class Helmet extends Component implements Serializable {

    public int index;

    public Helmet() {
    }

    public Helmet(int index) {
        this.index = index;
    }

}
