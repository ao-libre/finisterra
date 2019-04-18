package entity.character.status;

import com.artemis.Component;

import java.io.Serializable;

public class Level extends Component implements Serializable {

    public int level;

    public Level() {
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
