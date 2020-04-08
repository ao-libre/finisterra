package game.systems.resources;

import com.artemis.annotations.Wire;
import component.entity.character.info.SpellBook;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.systems.PlayerSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.model.Spell;

import java.util.Arrays;
import java.util.Optional;

@Wire
public class SpellsSystem extends PassiveSystem {

    private AOAssetManager assetManager;
    private PlayerSystem playerSystem;

    @Override
    protected void initialize() {
        super.initialize();
        this.assetManager = AOGame.getGlobalAssetManager();
    }

    public Optional<Spell> getSpell(int id) {
        return Optional.ofNullable(assetManager.getSpells().get(id));
    }

    public Spell[] getSpells() {
        final SpellBook spellBook = playerSystem.get().getSpellBook();

        return Arrays.stream(spellBook.spells).map(this::getSpell).filter(Optional::isPresent).map(Optional::get)
                .distinct().toArray(Spell[]::new);
    }

}
