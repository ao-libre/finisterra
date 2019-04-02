package map;

import com.artemis.Component;

public class Cave extends Component {

    public boolean[][] tiles;
    public int width;
    public int height;

    private Cave() {}

    public Cave(boolean [][] tiles, int width, int height) {
        this.tiles = tiles;
        this.width = width;
        this.height = height;
    }
}
