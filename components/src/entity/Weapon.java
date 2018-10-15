package entity;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.io.Serializable;

@DelayedComponentRemoval
public class Weapon extends Component implements Serializable {

    public int index;

    public Weapon() {
    }

    public Weapon(int index) {
        this.index = index;
    }

}
