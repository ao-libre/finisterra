package game.handlers;

import com.badlogic.gdx.Gdx;
import entity.character.info.SpellBook;
import game.AOGame;
import game.screens.GameScreen;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.Spell;

import java.util.Arrays;
import java.util.Optional;

import static com.artemis.E.E;

public class SpellHandler extends PassiveSystem {

    private AOAssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        assetManager = game.getAssetManager();
    }

    public Optional<Spell> getSpell(int id) {
        return Optional.ofNullable(assetManager.getSpells().get(id));
    }

    public Spell[] getSpells() {
        final int player = GameScreen.getPlayer();
        final SpellBook spellBook = E(player).getSpellBook();

        return Arrays.stream(spellBook.spells).map(this::getSpell).filter(Optional::isPresent).map(Optional::get)
                .distinct().toArray(Spell[]::new);
    }

}
