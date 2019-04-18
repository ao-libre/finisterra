package entity.character.parts;

import com.artemis.Component;

import java.io.Serializable;

public class Head extends Component implements Serializable {

    public int index;

    public Head() {
    }

    public Head(int index) {
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
