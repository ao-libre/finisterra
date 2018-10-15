package entity;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.io.Serializable;

@DelayedComponentRemoval
public class Object extends Component implements Serializable {

    public int index;

    public int count;

    public Object() {
    }

    public Object(int objectIndex) {
        this.index = objectIndex;
    }
}
