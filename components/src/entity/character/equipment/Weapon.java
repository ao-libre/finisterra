package entity.character.equipment;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.PooledWeaver;
import entity.Index;

import java.io.Serializable;

@PooledWeaver
@DelayedComponentRemoval
public class Weapon extends Component implements Serializable, Index {

    public int index;

    public Weapon() {
    }

    public Weapon(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
