package component.entity.character.states;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class CanWrite extends Component implements Serializable {
    public CanWrite() {
    }
}
