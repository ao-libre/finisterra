package component.entity;

import com.artemis.Component;

public class Ref extends Component {
    private int id;

    public Ref() {}

    public Ref(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
