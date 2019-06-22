package server.database;

import shared.model.Spell;
import shared.model.loaders.NPCLoader;
import shared.model.loaders.ObjectLoader;
import shared.model.loaders.SpellLoader;
import shared.model.map.Map;
import shared.model.npcs.NPC;
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
        InputStream objectsStream = ServerDescriptorReader.class.getClassLoader().getResourceAsStream("obj.dat");
        return reader.read(objectsStream, loader);
    }

    @Override
    public java.util.Map<Integer, Spell> loadSpells(String spells) {
        Reader<java.util.Map<Integer, Spell>> reader = new Reader<>();
        SpellLoader loader = new SpellLoader();
        InputStream objectsStream = ServerDescriptorReader.class.getClassLoader().getResourceAsStream("hechizos.dat");
        return reader.read(objectsStream, loader);
    }

    public java.util.Map<Integer, NPC> loadNPCs(String npcs) {
        Reader<java.util.Map<Integer, NPC>> reader = new Reader<>();
        NPCLoader loader = new NPCLoader();
        InputStream objectsStream = ServerDescriptorReader.class.getClassLoader().getResourceAsStream("NPCs.dat");
        return reader.read(objectsStream, loader);
    }

}
