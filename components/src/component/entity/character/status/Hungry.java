package component.entity.character.status;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Hungry extends Component implements Serializable {

    public int min;
    public int max;

    public Hungry() {
    }

}
