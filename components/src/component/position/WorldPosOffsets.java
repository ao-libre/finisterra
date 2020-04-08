package component.position;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

/*
 * Client level component, should not be present on server
 */
@PooledWeaver
public class WorldPosOffsets extends Component implements Serializable {

    public float x;
    public float y;

    public WorldPosOffsets(float pX, float pY) {
        this.x = pX;
        this.y = pY;
    }

    public WorldPosOffsets() {
        this.x = 0;
        this.y = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
