package component.entity.character.info;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Name extends Component implements Serializable {

    public String text;

    public Name() {
    }

    public Name(String name) {
        this.text = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
