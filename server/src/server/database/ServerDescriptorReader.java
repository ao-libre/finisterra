package server.database;

import shared.model.Spell;
import shared.model.loaders.ObjectLoader;
import shared.model.loaders.SpellLoader;
import shared.model.map.Map;
import shared.model.readers.DescriptorsReader;
import shared.model.readers.Reader;
import shared.objects.types.Obj;

import java.io.InputStream;

public class ServerDescriptorReader implements DescriptorsReader {
    @Override
    public Map loadMap(String map) {
        return null;
    }

    @Override
    public java.util.Map<Integer, Obj> loadObjects(String objects) {
        Reader<java.util.Map<Integer, Obj>> reader = new Reader<>();
        ObjectLoader loader = new ObjectLoader();
        InputStream objectsStream = ServerDescriptorReader.class.getClassLoader().getResourceAsStream("init/obj.dat");
        return reader.read(objectsStream, loader);
    }

    @Override
    public java.util.Map<Integer, Spell> loadSpells(String spells) {
        Reader<java.util.Map<Integer, Spell>> reader = new Reader<>();
        SpellLoader loader = new SpellLoader();
        InputStream objectsStream = ServerDescriptorReader.class.getClassLoader().getResourceAsStream("init/hechizos.dat");
        return reader.read(objectsStream, loader);
    }


}
