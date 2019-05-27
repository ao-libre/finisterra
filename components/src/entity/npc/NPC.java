package entity.npc;

import com.artemis.Component;

import java.io.Serializable;

public class NPC extends Component implements Serializable {

    public int id;

    public NPC() {}

    public NPC(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
