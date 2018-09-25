package entity;

import com.artemis.Component;

import java.io.Serializable;

public class Weapon extends Component implements Serializable {

    public int index;

    public Weapon() {
    }

    public Weapon(int index) {
        this.index = index;
    }

}
