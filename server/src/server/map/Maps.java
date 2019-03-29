package server.map;

import com.artemis.E;
import server.core.Server;
import shared.map.model.MapDescriptor;

import static com.artemis.E.E;

public class Maps {

    public static int mapEntity;

    public static void generateMapEntity(MapDescriptor descriptor, String path) {
        int[][] tiles = MapGenerator.generateMap(descriptor);
        mapEntity = Server.getWorld().create();

        E map = E(mapEntity).map();
        map.mapTiles(tiles);
        map.mapPath(path);
        map.mapHeight(descriptor.getMapHeight());
        map.mapWidth(descriptor.getMapWidth());

    }

}
