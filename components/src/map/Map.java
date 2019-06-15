package map;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

import java.io.Serializable;

@PooledWeaver
public class Map extends Component implements Serializable {

    public int[][] tiles;

    // tileset config file path
    public String path;
    public int width;
    public int height;

    public Map() {
    }

    public Map(int[][] tiles, String path, int width, int height) {
        this.tiles = tiles;
        this.path = path;
        this.width = width;
        this.height = height;
    }

}
