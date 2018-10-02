package position;

import com.artemis.Component;

import java.io.Serializable;

/*
 * Client level component, should not be present on server
 */
public class Pos2D extends Component implements Serializable {

    public float x;
    public float y;

    public Pos2D(float pX, float pY) {
        this.x = pX;
        this.y = pY;
    }

    public Pos2D() {
        this.x = 0;
        this.y = 0;
    }

}
