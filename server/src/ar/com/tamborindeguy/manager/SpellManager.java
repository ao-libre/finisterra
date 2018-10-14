package ar.com.tamborindeguy.manager;

import ar.com.tamborindeguy.database.ServerDescriptorReader;
import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.model.readers.DescriptorsReader;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import com.esotericsoftware.minlog.Log;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellManager {
    private static DescriptorsReader reader = new ServerDescriptorReader();
    private static Map<Integer, Spell> spells;

    public static void load() {
        Log.info("Loading spells...");
        spells = reader.loadSpells("hechizos");
    }

    public static Optional<Spell> getSpell(int id) {
        return Optional.ofNullable(spells.get(id));
    }

}
