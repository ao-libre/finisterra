package map;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class Cave extends Component {

    public boolean[][] tiles;
    public int width;
    public int height;

    public Cave() {
    }

    public Cave(boolean[][] tiles, int width, int height) {
        this.tiles = tiles;
        this.width = width;
        this.height = height;
    }

    public boolean isBlocked(int x, int y) {
        return !inRange(x, y) || tiles[x][y];
    }

    private boolean inRange(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
