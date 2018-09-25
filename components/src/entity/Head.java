package entity;

import com.artemis.Component;

import java.io.Serializable;

public class Head extends Component implements Serializable {

    public int index;

    public Head() {
    }

    public Head(int index) {
        this.index = index;
    }

}
