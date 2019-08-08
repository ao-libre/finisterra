package entity.character.equipment;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;
import entity.Index;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class Shield extends Component implements Serializable, Index {

    public int index;

    public Shield() {
    }

    public Shield(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
