package entity.character.info;

import com.artemis.Component;

import java.io.Serializable;

public class HeroClass extends Component implements Serializable {
    public int classId;

    public HeroClass() {}

    public HeroClass(int id) {
        classId = id;
    }

}
