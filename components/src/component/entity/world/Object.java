package component.entity.world;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class Object extends Component implements Serializable {

    public int index;

    public int count;

    public Object() {
    }

    public Object(int objectIndex) {
        this.index = objectIndex;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
