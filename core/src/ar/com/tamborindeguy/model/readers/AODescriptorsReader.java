package ar.com.tamborindeguy.model.readers;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.model.loaders.MapLoader;
import ar.com.tamborindeguy.model.loaders.ObjectLoader;
import ar.com.tamborindeguy.model.loaders.SpellLoader;
import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.objects.types.Obj;

public class AODescriptorsReader implements DescriptorsReader {
    @Override
    public Map loadMap(String map) {
        Reader<Map> reader = new Reader<>();
        MapLoader loader = new MapLoader();
        return reader.read(AO.GAME_MAPS_PATH + "Mapa" + map + ".map", loader);
    }

    @Override
    public java.util.Map<Integer, Obj> loadObjects(String objects) {
        Reader<java.util.Map<Integer, Obj>> reader = new Reader<>();
        ObjectLoader loader = new ObjectLoader();
        return reader.read(AO.GAME_INIT_PATH + objects + ".dat", loader);
    }

    @Override
    public java.util.Map<Integer, Spell> loadSpells(String spells) {
        Reader<java.util.Map<Integer, Spell>> reader = new Reader<>();
        return reader.read(AO.GAME_INIT_PATH + spells + ".dat", new SpellLoader());
    }

}
