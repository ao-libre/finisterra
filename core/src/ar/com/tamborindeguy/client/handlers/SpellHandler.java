package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.model.readers.AODescriptorsReader;
import ar.com.tamborindeguy.model.readers.DescriptorsReader;
import ar.com.tamborindeguy.model.textures.GameTexture;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
