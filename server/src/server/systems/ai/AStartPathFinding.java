package server.systems.ai;

import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import component.position.WorldPos;

public class AStartPathFinding {
    private static final int[][] NEIGHBORHOOD = new int[][]{
            new int[]{-1, 0},
            new int[]{0, -1},
            new int[]{0, 1},
            new int[]{1, 0}
    };
    public final AStarMap map;
    private final PathFinder<Node> pathfinder;
    private final Heuristic<Node> heuristic;
    private final GraphPath<Connection<Node>> connectionPath;

    public AStartPathFinding(AStarMap map) {
        this.map = map;
        this.pathfinder = new IndexedAStarPathFinder<>(createGraph(map));
        this.connectionPath = new DefaultGraphPath<>();
        this.heuristic = (node, endNode) -> {
            // Manhattan distance
            return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
        };
    }

    public static MyGraph createGraph(AStarMap map) {
        final int height = map.getHeight();
        final int width = map.getWidth();
        MyGraph graph = new MyGraph(map);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Node node = map.getNodeAt(x, y);
                if (node.isWall) {
                    continue;
                }
                // Add a connection for each valid neighbor
                for (int offset = 0; offset < NEIGHBORHOOD.length; offset++) {
                    int neighborX = node.x + NEIGHBORHOOD[offset][0];
                    int neighborY = node.y + NEIGHBORHOOD[offset][1];
                    if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                        Node neighbor = map.getNodeAt(neighborX, neighborY);
                        if (!neighbor.isWall) {
                            // Add connection to walkable neighbor
                            node.getConnections().add(new DefaultConnection<>(node, neighbor));
                        }
                    }
                }
                node.getConnections().shuffle();
            }
        }
        return graph;
    }

    public Node findNextNode(WorldPos source, WorldPos target) {
        int sourceX = MathUtils.floor(source.x);
        int sourceY = MathUtils.floor(source.y);
        int targetX = MathUtils.floor(target.x);
        int targetY = MathUtils.floor(target.y);

        if (map == null
                || sourceX < 0 || sourceX >= map.getWidth()
                || sourceY < 0 || sourceY >= map.getHeight()
                || targetX < 0 || targetX >= map.getWidth()
                || targetY < 0 || targetY >= map.getHeight()) {
            return null;
        }

        Node sourceNode = map.getNodeAt(sourceX, sourceY);
        Node targetNode = map.getNodeAt(targetX, targetY);
        connectionPath.clear();
        pathfinder.searchConnectionPath(sourceNode, targetNode, heuristic, connectionPath);

        return connectionPath.getCount() == 0 ? null : connectionPath.get(0).getToNode();
    }

    private static class MyGraph implements IndexedGraph<Node> {

        AStarMap map;

        public MyGraph(AStarMap map) {
            this.map = map;
        }

        @Override
        public int getIndex(Node node) {
            return node.getIndex();
        }

        @Override
        public Array<Connection<Node>> getConnections(Node fromNode) {
            return fromNode.getConnections();
        }

        @Override
        public int getNodeCount() {
            return map.getHeight() * map.getHeight();
        }

    }
}
