package game.handlers;

import model.readers.AODescriptorsReader;
import shared.model.Spell;
import shared.model.readers.DescriptorsReader;

import java.util.Map;
import java.util.Optional;

public class SpellHandler {

    private static DescriptorsReader reader = new AODescriptorsReader();
    private static Map<Integer, Spell> spells;

    public static void load() {
        spells = reader.loadSpells("hechizos");
    }

    public static Optional<Spell> getSpell(int id) {
        return Optional.ofNullable(spells.get(id));
    }

    public static Spell[] getSpells() {
        return spells.values().toArray(new Spell[spells.size()]);
    }

}
