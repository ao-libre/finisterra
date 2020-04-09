package component.entity.character;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Character extends Component implements Serializable {
    public Character() {
    }
}
