package entity.character.info;

import com.artemis.Component;

import java.io.Serializable;

public class Class extends Component implements Serializable {
    private int classId;

    public Class() {}

    public Class(int id) {
        classId = id;
    }

    public int getClassId() {
        return classId;
    }
}
