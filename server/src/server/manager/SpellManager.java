package server.manager;

import com.esotericsoftware.minlog.Log;
import server.core.Server;
import server.database.ServerDescriptorReader;
import shared.model.Spell;
import shared.model.readers.DescriptorsReader;

import java.util.Map;
import java.util.Optional;


/**
 * Spell Logic
 */
public class SpellManager implements IManager {
    private static DescriptorsReader reader = new ServerDescriptorReader();
    private static Map<Integer, Spell> spells;

    public SpellManager() {
        init();
    }

    public void init() {
        Log.info("Loading spells...");
        spells = reader.loadSpells("hechizos");
    }

    public int getId(Spell spell) {
        return spells.entrySet().stream().filter(entry -> entry.getValue().equals(spell)).map(Map.Entry::getKey).findFirst().get();
    }

    public Optional<Spell> getSpell(int id) {
        return Optional.ofNullable(spells.get(id));
    }

    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    @Override public Server getServer() {
        return null;
    }
}
