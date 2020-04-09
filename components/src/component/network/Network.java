package component.network;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Network extends Component implements Serializable {
    public int id;

    public Network() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
