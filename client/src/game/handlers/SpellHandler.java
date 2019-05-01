package game.handlers;

import com.badlogic.gdx.Gdx;
import entity.character.info.SpellBook;
import game.screens.GameScreen;
import model.readers.AODescriptorsReader;
import shared.model.Spell;
import shared.model.readers.DescriptorsReader;
import shared.util.SharedResources;
import shared.util.SpellJson;

import java.util.*;

import static com.artemis.E.E;

public class SpellHandler {

    private static DescriptorsReader reader = new AODescriptorsReader();
    public static Map<Integer, Spell> spells = new HashMap<>();

    public static void load() {
        SpellJson.load(spells, Gdx.files.internal(SharedResources.SPELLS_JSON_FILE));
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
