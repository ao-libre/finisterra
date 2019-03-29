package map;

import com.artemis.Component;

import java.io.Serializable;

public class Map extends Component implements Serializable {

    public int[][] tiles;

    // tileset config file path
    public String path;
    public int width;
    public int height;

    public Map() {}

    public Map(int[][] tiles, String path, int width, int height) {
        this.tiles = tiles;
        this.path = path;
        this.width = width;
        this.height = height;
    }

}
