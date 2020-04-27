package server.systems.world.entity.ai;

public class AStarMap {

    private final int width;
    private final int height;
    private Node[][] map;

    public AStarMap(int width, int height) {
        this.width = width;
        this.height = height;

        map = new Node[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = new Node(this, x, y);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Node getNodeAt(int x, int y) {
        return map[y][x];
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                stringBuilder.append(map[y][x].isWall ? "#" : " ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
