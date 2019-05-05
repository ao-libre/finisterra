package entity.character.equipment;

import com.artemis.Component;

import java.io.Serializable;

public class Armor extends Component implements Serializable {
    private int index;

    public Armor() {
    }

    public Armor(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
