package entity.character.parts;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import entity.Index;

import java.io.Serializable;

@PooledWeaver
public class Body extends Component implements Serializable, Index {

    public int index;

    public Body() {
    }

    public Body(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
