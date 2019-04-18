package network;

import com.artemis.Component;

import java.io.Serializable;

public class Network extends Component implements Serializable {
    public int id;

    public Network() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
