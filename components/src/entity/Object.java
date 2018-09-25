package entity;

import com.artemis.Component;

import java.io.Serializable;

public class Object extends Component implements Serializable {
    public int index;

    public Object() {
    }

    public Object(int objectIndex) {
        this.index = objectIndex;
    }
}
