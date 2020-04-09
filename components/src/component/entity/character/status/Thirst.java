package component.entity.character.status;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Thirst extends Component implements Serializable {

    public int min;
    public int max;

    public Thirst() {
    }
}
