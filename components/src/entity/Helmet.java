package entity;

import com.artemis.Component;

import java.io.Serializable;

public class Helmet extends Component implements Serializable {

    public int index;

    public Helmet() {
    }

    public Helmet(int index) {
        this.index = index;
    }

}
