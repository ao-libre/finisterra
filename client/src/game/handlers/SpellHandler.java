package game.handlers;

import entity.character.info.SpellBook;
import game.screens.GameScreen;
import model.readers.AODescriptorsReader;
import shared.model.Spell;
import shared.model.readers.DescriptorsReader;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.artemis.E.E;

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
        final int player = GameScreen.getPlayer();
        final SpellBook spellBook = E(player).getSpellBook();

        return Arrays.stream(spellBook.spells).map(SpellHandler::getSpell).filter(Optional::isPresent).map(Optional::get)
            .distinct().toArray(Spell[]::new);
    }

}
