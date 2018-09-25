package entity;

import com.artemis.Component;

import java.io.Serializable;

public class Shield extends Component implements Serializable {

    public int index;

    public Shield() {
    }

    public Shield(int index) {
        this.index = index;
    }

}
