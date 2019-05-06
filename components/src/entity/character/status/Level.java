package entity.character.status;

import com.artemis.Component;

import java.io.Serializable;

public class Level extends Component implements Serializable {

    public int level;

    public Level() {
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
