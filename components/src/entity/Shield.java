package entity;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.io.Serializable;

@DelayedComponentRemoval
public class Shield extends Component implements Serializable {

    public int index;

    public Shield() {
    }

    public Shield(int index) {
        this.index = index;
    }

}
