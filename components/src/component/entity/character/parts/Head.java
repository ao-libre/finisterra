package component.entity.character.parts;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.entity.Index;

import java.io.Serializable;

@PooledWeaver
public class Head extends Component implements Serializable, Index {

    public int index;

    public Head() {
    }

    public Head(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
