package entity.character.status;

import com.artemis.Component;

import java.io.Serializable;

public class Health extends Component implements Serializable {

    public int min;
    public int max;

    public Health() {
    }

}
