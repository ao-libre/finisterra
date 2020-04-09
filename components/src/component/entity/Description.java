package component.entity;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Description extends Component implements Serializable {

    public String text;

    public Description() {
    }

    public Description(String desc) {
        this.text = desc;
    }

}
