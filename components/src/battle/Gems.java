package battle;

import com.artemis.Component;

import java.io.Serializable;

public class Gems extends Component implements Serializable {

    private int red;
    private int blue;

    public Gems() {}

    public int getBlue() {
        return blue;
    }

    public int getRed() {
        return red;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setRed(int red) {
        this.red = red;
    }
}
