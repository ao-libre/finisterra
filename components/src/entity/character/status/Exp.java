package entity.character.status;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Exp extends Component implements Serializable {

    public int exp;

    public Exp() {
    }

}
