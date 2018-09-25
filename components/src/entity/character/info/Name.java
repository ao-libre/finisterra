package entity.character.info;

import com.artemis.Component;

import java.io.Serializable;

public class Name extends Component implements Serializable{

    public String text;

    public Name() {}

    public Name(String name) {
        this.text = name;
    }

}
