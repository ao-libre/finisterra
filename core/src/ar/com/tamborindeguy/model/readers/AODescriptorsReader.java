package ar.com.tamborindeguy.model.readers;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.model.Objects;
import ar.com.tamborindeguy.model.loaders.MapLoader;
import ar.com.tamborindeguy.model.loaders.ObjectLoader;
import ar.com.tamborindeguy.model.map.Map;

public class AODescriptorsReader implements DescriptorsReader {
    @Override
    public Map loadMap(String map) {
        Reader<Map> reader = new Reader<Map>();
        MapLoader loader = new MapLoader();
        return reader.read(AO.GAME_MAPS_PATH + "Mapa" + map + ".map", loader);
    }

    @Override
    public Objects loadObjects(String objects) {
        Reader<Objects> reader = new Reader<>();
        ObjectLoader loader = new ObjectLoader();
        return reader.read(AO.GAME_INIT_PATH + objects + ".dat", loader);
    }


}
