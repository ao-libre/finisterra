package model.readers;

import game.AOGame;
import shared.model.Spell;
import model.loaders.MapLoader;
import shared.model.loaders.ObjectLoader;
import shared.model.loaders.SpellLoader;
import shared.model.map.Map;
import shared.model.readers.DescriptorsReader;
import shared.model.readers.Reader;
import shared.objects.types.Obj;

public class AODescriptorsReader implements DescriptorsReader {
    @Override
    public Map loadMap(String map) {
        Reader<Map> reader = new Reader<>();
        MapLoader loader = new MapLoader();
        return reader.read(AOGame.GAME_MAPS_PATH + "Mapa" + map + ".map", loader);
    }

    @Override
    public java.util.Map<Integer, Obj> loadObjects(String objects) {
        Reader<java.util.Map<Integer, Obj>> reader = new Reader<>();
        ObjectLoader loader = new ObjectLoader();
        return reader.read(AOGame.GAME_INIT_PATH + objects + ".dat", loader);
    }

    @Override
    public java.util.Map<Integer, Spell> loadSpells(String spells) {
        Reader<java.util.Map<Integer, Spell>> reader = new Reader<>();
        return reader.read(AOGame.GAME_INIT_PATH + spells + ".dat", new SpellLoader());
    }

}
