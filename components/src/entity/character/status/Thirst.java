package entity.character.status;

import com.artemis.Component;

import java.io.Serializable;

public class Thirst extends Component implements Serializable {

    public int min;
    public int max;

    public Thirst() {
    }
}
