package ar.com.tamborindeguy.database;

import ar.com.tamborindeguy.manager.MapManager;
import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.model.loaders.ObjectLoader;
import ar.com.tamborindeguy.model.loaders.SpellLoader;
import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.model.readers.DescriptorsReader;
import ar.com.tamborindeguy.model.readers.Reader;
import ar.com.tamborindeguy.objects.types.Obj;

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
