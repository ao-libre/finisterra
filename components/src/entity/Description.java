package entity;

import com.artemis.Component;

import java.io.Serializable;

public class Description extends Component implements Serializable {

    public String text;

    public Description() {
    }

    public Description(String desc) {
        this.text = desc;
    }

}
