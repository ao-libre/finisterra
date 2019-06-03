package model.readers;

import shared.model.Spell;
import shared.model.loaders.ObjectLoader;
import shared.model.loaders.SpellLoader;
import shared.model.map.Map;
import shared.model.readers.DescriptorsReader;
import shared.model.readers.Reader;
import shared.objects.types.Obj;
import shared.util.SharedResources;

public class AODescriptorsReader implements DescriptorsReader {

    @Override
    public Map loadMap(String map) {
        return null;
    }

    @Override
    public java.util.Map<Integer, Obj> loadObjects(String objects) {
        Reader<java.util.Map<Integer, Obj>> reader = new Reader<>();
        ObjectLoader loader = new ObjectLoader();
        return reader.read(SharedResources.OBJECTS_FOLDER + objects + ".dat", loader);
    }

    @Override
    public java.util.Map<Integer, Spell> loadSpells(String spells) {
        Reader<java.util.Map<Integer, Spell>> reader = new Reader<>();
        return reader.read(SharedResources.SPELLS_FOLDER + spells + ".dat", new SpellLoader());
    }

}
